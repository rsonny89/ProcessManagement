package com.rsonny.process;

import java.util.LinkedList;

/**
 * Interface to be implemented by a scheduler class.
 */
public interface Scheduler {
  /**
   * Adds a process to the current ready queue. It should insert it in sorted order.
   *
   * @param queue Current ready queue.
   * @param process Process to add.
   */
  void add(LinkedList<Process> queue, Process process);

  /**
   * Picks the next process to run from the current ready queue and current process.
   * If the current process is not picked, it should be scheduled back onto the ready queue.
   *
   * @param queue Current ready queue.
   * @param current Current running process.
   * @return Next process to run.
   */
  Process schedule(LinkedList<Process> queue, Process current);
}
