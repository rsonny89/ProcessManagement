package com.rsonny.process;


import java.util.concurrent.ThreadLocalRandom;

public class Process {
  /**
   * Different types of actions the process can before while running.
   */
  public enum Action {
    PROCESS(""), IO("IO"), END("END");

    private String name;

    private Action(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  // Array of actions that will be performed on each CPU cycle spent on
  // performing this process.
  private Action[] runtime;

  // Number of CPU cycles this process has run.
  private int currentCycle = 0;

  // Number of CPU cycles the process has waited.
  private int waitCycleCount = 0;

  // Unique process ID.
  private int id;

  /**
   * Creates a new process.
   * @param id Identifier of the process.
   * @param actions Number of actions the process should contain.
   * @param calls Number of actions that should be IO calls.
   */
  public Process(int id, int actions, int calls) {
    this.id = id;
    this.runtime = new Action[actions];

    for (int i = 0; i < actions; i++) {
      this.runtime[i] = Action.PROCESS;
    }

    calls = calls >= actions ? actions : calls;

    int lastIO = 0;
    int max = actions - calls + 1;

    for (int i = 0; i < calls && lastIO < actions; i++) {
      lastIO = ThreadLocalRandom.current().nextInt(lastIO, max);
      this.runtime[lastIO] = Action.IO;
      lastIO += 1;
      max += 1;
    }
  }

  /**
   * Runs the process through one CPU cycle.
   * @return The action the process does this cycle.
   */
  public Action run() {
    if (currentCycle >= runtime.length) return Action.END;
    
    Action action = runtime[currentCycle];
    currentCycle += 1;
    
    return action;
  }

  /**
   * Informs the process that it is currently waiting to be executed.
   */
  public void addWaitCycle() {
    this.waitCycleCount += 1;
  }

  @Override
  public String toString() {
    return String.format("%d", this.id);
  }

  /**
   * Returns the process ID.
   * @return Process ID.
   */
  public int getId() { return id; }

  /**
   * Calculates and returns the remaining CPU cycles the process has left.
   * @return Remaining CPU cycles left.
   */
  public int getRemainingCycles() {
    return runtime.length - currentCycle + 1;
  }

  /**
   * Returns the number of seconds the process sat in the waiting queue.
   * @return Number of seconds the process sat in the waiting queue.
   */
  public int getWaitCycleCount() {
    return this.waitCycleCount;
  }

  /**
   * Returns the number of IO calls contained in this process.
   * @return Number of IO calls in this process.
   */
  public int getIOCallCount() {
    int count = 0;

    for (Action action: runtime) {
      if (action == Action.IO) count += 1;
    }

    return count;
  }

  /**
   * Returns the total number of cycles in this process.
   * @return Total number of cycles in this process.
   */
  public int getCycles() {
    return runtime.length;
  }
}
