package com.github.rsonny;


public class QueuedProcess {
  private int cycle;
  private Process process;

  /**
   * Process that is waiting for the next associated cycle.
   * @param cycle Cycle the process is waiting for.
   * @param process Process that is waiting for the cycle.
   */
  public QueuedProcess(int cycle, Process process) {
    this.cycle = cycle;
    this.process = process;
  }

  /**
   * Returns the cycle number.
   * @return Cycle number.
   */
  public int getCycle() { return cycle; }

  /**
   * Returns the waiting process.
   * @return Waiting process.
   */
  public Process getProcess() { return process; }
}
