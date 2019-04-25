package com.github.rsonny;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

/**
 * Hello world!
 *
 */
public class Runner {
  @Parameter(names={"--count", "-c"}, description = "Number of processes to create")
  private int count = 10;

  @Parameter(names={"--top", "-t"}, description = "Lower limit of the process runtime value")
  private int top = 10;

  @Parameter(names={"--bottom", "-b"}, description = "Upper limit of the process runtime value")
  private int bottom = 1;

  @Parameter(names={"--io"}, description = "Maximum number of IO calls the process will make")
  private int io = 2;

  @Parameter(names = "--help", help = true, description = "Prints possible options for the CLI tool")
  private boolean help = false;

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
    LinkedList<Process> ready = new LinkedList<>();
    InterruptQueue interrupts = new InterruptQueue();
    Scheduler scheduler = new FCFSScheduler();
    int start = 1;

    System.out.printf("Creating %d processes\n", count);

    // Create x processes as interrupts to run at set intervals.
    for (int i = 0; i < count; i++) {
      int actions = ThreadLocalRandom.current().nextInt(bottom, top);
      Process process = new Process(i + 1, actions, io);

      interrupts.add(new Interrupt(Interrupt.Type.NEW_PROCESS, start, process));
      start += ThreadLocalRandom.current().nextInt(1, 10);
    }

    System.out.println();

    int cycle = 0;
    Process cached = null;
    Process current = null;

    System.out.println("Cycle Action    PID Type");

    while (current != null || cached != null || !ready.isEmpty() || !interrupts.isEmpty()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException error) {
        System.out.println("Received unexpected interrupt");
      }

      cycle += 1;
      Interrupt interrupt = interrupts.next(cycle);

      // Interrupts take precedence over all other actions.
      if (interrupt != null) {
        System.out.printf("%5d Interrupt %-3s %s\n", cycle, interrupt.getProcess(), interrupt.getType());

        ready.add(interrupt.getProcess());

        // Cache the current process if one exists.
        if (cached == null && current != null) {
          cached = current;
          current = null;
        }

        continue;
      }

      // If there is no current process set, but there is a cached process or there are
      // are processes in the ready queue, run the scheduler and move onto the next cycle.
      if (current == null && (cached != null || !ready.isEmpty())) {
        current = scheduler.schedule(ready, cached);
        System.out.printf("%5d Schedule  %s\n", cycle, current);

        if (cached != null) cached = null;

        continue;
      }

      // At this point if there is no scheduled process, we're just waiting for an IO
      // or new process interrupt. Continue to the next cycle.
      if (current == null) {
        System.out.printf("%5d Wait\n", cycle);

        continue;
      }

      // Run one tick of the current process and print the action.
      Process.Action action = current.process();
      System.out.printf("%5d Run       %-3s %s\n", cycle, current, action);

      // If the action was an IO call, then generate a random IO delay and set the
      // current process to null to it runs the scheduler again.
      if (action == Process.Action.IO) {
        int delay = cycle + ThreadLocalRandom.current().nextInt(2, 20);

        interrupts.add(new Interrupt(Interrupt.Type.IO_FINISHED, delay, current));
        current = null;

        continue;
      }

      // Remove the current process completely if it's done.
      if (action == Process.Action.END) current = null;
    }
  }
}
