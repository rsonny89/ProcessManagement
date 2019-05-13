package com.rsonny.process;

import java.util.LinkedList;

/**
 * Implementation of the first come first served scheduler.
 */
public class FCFSScheduler implements Scheduler {
  /**
   * Implementation of the schedule method. Returns the current process is one exists,
   * otherwise it pulls from the top of the queue.
   *
   * @param queue Current ready queue.
   * @param current Current process on the CPU.
   * @return Next process to run.
   */
  public Process schedule(LinkedList<Process> queue, Process current) {
    if (current != null) return current;

    return queue.isEmpty() ? null : queue.remove();
  }

  /**
   * Implementation of the add method. Adds the next process onto the end of the queue.
   * @param queue Current ready queue.
   * @param process Process to add to the queue.
   */
  public void add(LinkedList<Process> queue, Process process) {
    queue.add(process);
  }
}
