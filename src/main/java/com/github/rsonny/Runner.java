package com.github.rsonny;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.JCommander;

/**
 * Hello world!
 *
 */
public class Runner {
  @Parameter(names={"--count", "-c"}, description = "Number of processes to create")
  private int count = 20;

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

  private void run() {
    Queue<Process> ready = new LinkedList<>();
    Queue<QueuedProcess> wait = new LinkedList<>();
    Queue<QueuedProcess> processes = new LinkedList<>();
    int start = 1;

    System.out.printf("Creating %d processes\n", count);

    for (int i = 0; i < count; i++) {
      int actions = ThreadLocalRandom.current().nextInt(bottom, top);
      Process process = new Process(i + 1, actions, io);

      processes.add(new QueuedProcess(start, process));
      start += ThreadLocalRandom.current().nextInt(1, 3);

      System.out.printf("  Process %d Start %d\n", process.getId(), start);
    }

    int cycle = 0;
    Process current = null;

    while (!processes.isEmpty() || !ready.isEmpty() || !wait.isEmpty()) {
      cycle += 1;
      System.out.printf("-- %d\n", cycle);

      while (!wait.isEmpty() && wait.peek().getCycle() == cycle) {
        Process process = wait.remove().getProcess();

        System.out.printf("  Received IO interrupt for process %d\n", process.getId());
        System.out.printf("  Placing process %d onto ready queue\n", process.getId());

        ready.add(process);
      }

      if (!processes.isEmpty() && processes.peek().getCycle() == cycle) {
        Process process = processes.remove().getProcess();

        System.out.printf("  Received process %d\n", process.getId());

        ready.add(process);
      }

      if (current == null && !ready.isEmpty()) {
        current = ready.remove();
      }

      if (current == null) {
        System.out.println("  Ready queue is empty, waiting for next process\n");
      } else {
        System.out.printf("  Running process %d\n", current.getId());

        switch (current.process()) {
          case IO:
            int delay = cycle + ThreadLocalRandom.current().nextInt(2, 5);

            System.out.printf("  Process %d made an IO call delay %d\n", current.getId(), delay);
            System.out.printf("  Placing process %d onto wait queue\n", current.getId());

            wait.add(new QueuedProcess(delay, current));
            current = null;

            break;
          case END:
            System.out.printf("  Process %d is complete!\n", current.getId());

            current = null;
            break;
        }
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException error) {
        System.out.println("Received unexpected interrupt");
      }
    }
  }
}
