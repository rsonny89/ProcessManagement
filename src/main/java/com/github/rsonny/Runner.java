package com.github.rsonny;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

/**
 * Hello world!
 *
 */
public class Runner {
  @Parameter(names={"--count", "-c"}, description = "Number of processes to create")
  private int processCount = 10;

  @Parameter(names={"--top", "-t"}, description = "Upper limit of the process runtime value")
  private int processUpperLimit = 10;

  @Parameter(names={"--bottom", "-b"}, description = "Lower limit of the process runtime value")
  private int processLowerLimit = 1;

  @Parameter(names={"--io-calls"}, description = "Maximum number of IO calls a process can make")
  private int ioCallCount = 2;

  @Parameter(names={"--io-top"}, description = "Maximum number of cycles a process will wait for IO")
  private int ioUpperLimit = 10;

  @Parameter(names = {"--io-bottom"}, description = "Lowest number of cycles a process will wait for IO")
  private int ioLowerLimit = 1;

  @Parameter(names = "--help", help = true, description = "Prints possible options for the CLI tool")
  private boolean help = false;

  @Parameter(names = {"-i", "--interactive"}, description = "Runs the scheduler in interactive mode")
  private boolean isInteractive = false;

  @Parameter(names = {"-w", "--wait"}, description = "Time in milliseconds each cycle should take")
  private int waitTime = 500;

  @Parameter(names = {"--shortest"}, description = "Runs the shortest job scheduler")
  private boolean useShortestJob = false;

  @Parameter(names = {"-p", "--print-info"}, description = "If the scheduling info should be run in non interactive mode")
  private boolean printInfo = false;

  private Scanner scanner = new Scanner(System.in);

  public static void main( String[] args ) {
    Runner runner = new Runner();
    JCommander commander = JCommander.newBuilder().addObject(runner).build();

    commander.parse(args);

    if (runner.help) {
      commander.usage();
      return;
    }

    runner.run();
  }

  /**
   * Runner start point.
   */
  private void run() {
    InterruptQueue interrupts = new InterruptQueue();
    Scheduler scheduler = new FCFSScheduler();
    int start = 1;

    if (useShortestJob) scheduler = new ShortestJobScheduler();

    System.out.printf("Creating %d processes\n", processCount);
    System.out.println();
    System.out.println("PID Time");

    // Create x processes as interrupts to run at set intervals.
    for (int i = 0; i < processCount; i++) {
      int actions = ThreadLocalRandom.current().nextInt(processLowerLimit, processUpperLimit);
      Process process = new Process(i + 1, actions, ioCallCount);

      interrupts.add(new Interrupt(Interrupt.Type.NEW_PROCESS, start, process));
      start += ThreadLocalRandom.current().nextInt(1, 10);
      System.out.printf("%-3s %d\n", process, process.getRemainingTime());
    }

    System.out.println();

    int cycle = 0;
    int waitCycles = 0;
    Process cached = null;
    Process current = null;

    System.out.println("Cycle Action    PID Type");

    while (current != null || cached != null || !scheduler.isEmpty() || !interrupts.isEmpty()) {
      if (isInteractive && cycle != 0) {
        System.out.println();
        scheduler.print(current, cached);
        scanner.nextLine();
      }

      if (!isInteractive) {
        if (printInfo && cycle != 0) {
          System.out.println();
          scheduler.print(current, cached);
        }
        System.out.println();
        try {
          Thread.sleep(waitTime);
        } catch (InterruptedException error) {
          System.out.println("Received unexpected interrupt");
          continue;
        }
      }


      cycle += 1;
      Interrupt interrupt = interrupts.next(cycle);

      // Interrupts take precedence over all other actions.
      if (interrupt != null) {
        System.out.printf("%5d Interrupt %-3s %-3s  ", cycle, interrupt.getProcess(), interrupt.getType());
        scheduler.add(interrupt.getProcess());

        // Cache the current process if one exists.
        if (cached == null && current != null) {
          cached = current;
          current = null;
        }

        continue;
      }

      // If there is no current process set, but there is a cached process or there are
      // are processes in the ready queue, run the scheduler and move onto the next cycle.
      if (current == null && (cached != null || !scheduler.isEmpty())) {
        current = scheduler.schedule(cached);
        System.out.printf("%5d Schedule  %-3s      ", cycle, current);

        if (cached != null) cached = null;

        continue;
      }

      // At this point if there is no scheduled process, we're just waiting for an IO
      // or new process interrupt. Continue to the next cycle.
      if (current == null) {
        waitCycles += 1;
        System.out.printf("%5d Wait               ", cycle);

        continue;
      }

      // Run one tick of the current process and print the action.
      Process.Action action = current.process();
      System.out.printf("%5d Run       %-3s %-3s  ", cycle, current, action);

      // If the action was an IO call, then generate a random IO delay and set the
      // current process to null to it runs the scheduler again.
      if (action == Process.Action.IO) {
        int delay = cycle + ThreadLocalRandom.current().nextInt(ioLowerLimit, ioUpperLimit);

        interrupts.add(new Interrupt(Interrupt.Type.IO_FINISHED, delay, current));
        current = null;

        continue;
      }

      // Remove the current process completely if it's done.
      if (action == Process.Action.END) current = null;
    }

    double utilization = (waitCycles * (cycle * 1d)) / (cycle);

    System.out.println("\n\n--- STATISTICS ---");
    System.out.printf("CPU Utilization: %.0f\n", 100d - utilization);
  }
}
