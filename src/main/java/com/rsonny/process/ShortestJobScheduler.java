package com.rsonny.process;

import java.util.LinkedList;

/**
 * Shortest job first scheduler implementation. This will always run the
 * shortest job first.
 */
public class ShortestJobScheduler implements Scheduler {

  /**
   * Implementation of the schedule method for the scheduler interface.
   * If the current process has less cycles than the process at the top of the ready queue,
   * then it gets rescheduled and the process on the ready queue is scheduled. Otherwise it
   * returns the current process.
   *
   * @param queue Current ready queue.
   * @param current Current running process.
   * @return Next process to run.
   */
  public Process schedule(LinkedList<Process> queue, Process current) {
    if (current == null) {
      if (queue.isEmpty()) return null;

      return queue.remove();
    }

    if (queue.isEmpty()) return current;

    if (current.getRemainingCycles() <= queue.peek().getRemainingCycles()) {
      return current;
    }

    add(queue, current);

    return queue.remove();
  }

  /**
   * Implementation of the add method for the scheduler interface. Sorts the new process
   * into the current queue by the number of remaining cycles left.
   *
   * @param ready Current ready queue.
   * @param process Process to add.
   */
  public void add(LinkedList<Process> ready, Process process) {
    if (process == null) {
      return;
    }

    for (int i = 0; i < ready.size(); i++) {
      if (process.getRemainingCycles() < ready.get(i).getRemainingCycles()) {
        ready.add(i, process);
        return;
      }
    }

    ready.add(process);
  }
}
