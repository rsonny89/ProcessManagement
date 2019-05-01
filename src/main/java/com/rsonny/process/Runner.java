package com.rsonny.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

/**
 * Hello world!
 *
 */
public class Runner {
  @Parameter(
    names = {"--count", "-c"},
    description = "Number of processes to create.")
  private int processCount = 10;

  @Parameter(
    names = {"--runtime-max"},
    description = "Maximum number of cycles a process can take.")
  private int runtimeMax = 10;

  @Parameter(
    names = {"--runtime-min"},
    description = "Minimum number of cycles a process can take.")
  private int runtimeMin = 1;

  @Parameter(
    names = {"--io-max"},
    description = "Maximum number of IO calls a process can make.")
  private int ioMax = 2;

  @Parameter(
    names = {"--io-min"},
    description = "Minimum number of IO calls a process can make.")
  private int ioMin = 0;

  @Parameter(
    names = {"--wait-max"},
    description = "Maximum number of cycles a process will wait for IO.")
  private int waitMax = 10;

  @Parameter(
    names = {"--wait-min"},
    description = "Minimum number of cycles a process will wait for IO.")
  private int waitMin = 5;

  @Parameter(
    names = {"--start-max"},
    description = "Maximum number of cycles a new process will wait to start from the previous one.")
  private int startMax = 10;

  @Parameter(
    names = {"--start-min"},
    description = "Minumum number of cycles a new process will wait to start from the previous one.")
  private int startMin = 1;

  @Parameter(
    names = "--help",
    help = true,
    description = "Prints possible options for the CLI tool.")
  private boolean help = false;

  @Parameter(
    names = {"-i", "--interactive"},
    description = "Runs the scheduler in interactive mode.")
  private boolean interactive = false;

  @Parameter(
    names = {"--interval"},
    description = "Time in milliseconds each cycle should take.")
  private int interval = 500;

  @Parameter(
    names = {"--shortest"},
    description = "Runs the shortest job scheduler.")
  private boolean useShortestJob = false;

  private Scanner scanner = new Scanner(System.in);

  private LinkedList<Process> ready = new LinkedList<>();
  private InterruptQueue interrupts = new InterruptQueue();
  private Process current = null;
  private Process cached = null;
  private int cycle = 0;
  private int waitCycleCount = 0;
  private Scheduler scheduler = new FCFSScheduler();
  private ArrayList<Process> processes = new ArrayList<>();

  public static void main( String[] args ) throws InterruptedException {
    Runner runner = new Runner();
    JCommander commander = JCommander.newBuilder().addObject(runner).build();

    commander.parse(args);

    if (runner.help) {
      commander.usage();
      return;
    }

    runner.run();
  }

  private void printReadyQueue() {
    System.out.println(" └-----< QUEUE >--------------------");
    System.out.println("         ORDER  PID  CYCLES");

    if (current == null && cached == null) {
      System.out.println("         -      -    -");
    }

    if (current != null ) {
      System.out.printf("         -      %-3s  %d\n", current.getId(), current.getRemainingCycles());
    }

    if (cached != null) {
      System.out.printf("         -      %-3s  %d\n", cached.getId(), cached.getRemainingCycles());
    }

    for (int i = 0; i < ready.size(); i++) {
      Process process = ready.get(i);

      System.out.printf("         %-5d  %-3s  %d\n", i, process.getId(), process.getRemainingCycles());
    }
  }

  /**
   * Runner start point.
   */
  private void run() throws InterruptedException {
    int start = 1;

    System.out.println("==< OPTIONS >=======================");
    System.out.printf(" Count:    %d Processes\n", processCount);
    System.out.printf(" Runtime:  %d - %d Cycles\n", runtimeMin, runtimeMax);
    System.out.printf(" Start:    %d - %d Cycles\n", startMin, startMax);
    System.out.printf(" IO:       %d - %d Calls\n", ioMin, ioMax);
    System.out.printf(" Latency:  %d - %d Cycles\n", waitMin, waitMax);
    System.out.println();

    // Switch to the shortest job scheduler if the flag is set.
    if (useShortestJob) {
      scheduler = new ShortestJobScheduler();
    }

    // Create x processes as interrupts to run at set intervals.
    for (int i = 0; i < processCount; i++) {
      int actions = ThreadLocalRandom.current().nextInt(runtimeMin, runtimeMax);
      Process process = new Process(i + 1, actions, ThreadLocalRandom.current().nextInt(ioMin, ioMax + 1));

      processes.add(process);
      interrupts.add(new Interrupt(Interrupt.Type.NEW_PROCESS, start, process));
      start += ThreadLocalRandom.current().nextInt(startMin, startMax);
    }

    System.out.println("==< PROCESSES >=====================");
    System.out.println(" PID   IOP   CYCLES");

    for (Process process: processes) {
      System.out.printf(" %-3d   %-3d   %d\n", process.getId(), process.getIOCallCount(), process.getCycles());
    }

    System.out.println();
    System.out.println("==< START >=========================");
    System.out.print(" CYCLE   ACTION      PID   TYPE");

    while (current != null || cached != null || !ready.isEmpty() || !interrupts.isEmpty()) {
      System.out.println();

      if (cycle != 0) {
        printReadyQueue();

        if (interactive) {
          scanner.nextLine();
        } else {
          System.out.println();
        }
      }

      if (!interactive && interval > 0) {
        Thread.sleep(interval);
      }

      cycle += 1;
      Interrupt interrupt = interrupts.next(cycle);

      // Interrupts take precedence over all other actions.
      if (interrupt != null) {
        System.out.printf(" %-5d   Interrupt   %-3s   %-3s", cycle, interrupt.getProcess(), interrupt.getType());
        scheduler.add(ready, interrupt.getProcess());

        // Cache the current process if one exists.
        if (cached == null && current != null) {
          cached = current;
          current = null;
        }

        continue;
      }

      // If there is no current process set, but there is a cached process or there are
      // are processes in the ready queue, run the queue and move onto the next cycle.
      if (current == null && (cached != null || !ready.isEmpty())) {
        current = scheduler.schedule(ready, cached);
        System.out.printf(" %-5d   Schedule    %-3d", cycle, current.getId());

        if (cached != null) {
          cached = null;
        }

        continue;
      }

      // At this point if there is no scheduled process, we're just waiting for an IO
      // or new process interrupt. Continue to the next cycle.
      if (current == null) {
        waitCycleCount += 1;
        System.out.printf(" %-5d   Wait", cycle);

        continue;
      }

      // Add a wait cycle to every process in the ready queue.
      for (Process process: ready) {
        process.addWaitCycle();
      }

      // Run one tick of the current process and print the action.
      Process.Action action = current.run();
      System.out.printf(" %-5d   Run         %-3s   %-3s", cycle, current, action);

      // If the action was an IO call, then generate a random IO delay and set the
      // current process to null to it runs the queue again.
      if (action == Process.Action.IO) {
        int delay = cycle + ThreadLocalRandom.current().nextInt(waitMin, waitMax);

        interrupts.add(new Interrupt(Interrupt.Type.IO_FINISHED, delay, current));
        current = null;

        continue;
      }

      // Remove the current process completely if it's done.
      if (action == Process.Action.END) {
        current = null;
      }
    }

    double utilization = (waitCycleCount * (cycle * 1d)) / (cycle);

    System.out.println("\n\n==< STATISTICS >====================");
    System.out.printf(" CPU Utilization: %.0f\n\n", 100d - utilization);

    System.out.println(" PID  CYCLES  WAITING");

    for (Process process: processes) {
      System.out.printf(" %-3d  %-6d  %d\n", process.getId(), process.getCycles(), process.getWaitCycleCount());
    }
  }
}
