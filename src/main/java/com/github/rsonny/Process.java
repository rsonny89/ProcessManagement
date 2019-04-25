package com.github.rsonny;


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
  private int current = 0;

  // Unique process ID.
  private int id;

  public Process(int id, int actions, int io) {
    this.id = id;
    this.runtime = new Action[actions];

    for (int i = 0; i < actions; i++) {
      this.runtime[i] = Action.PROCESS;
    }

    int lastIO = 0;

    for (int i = 0; i < io; i++) {
      lastIO = ThreadLocalRandom.current().nextInt(lastIO, actions);

      if (lastIO != actions - 1) {
        this.runtime[lastIO] = Action.IO;
      }
    }
  }

  public Action process() {
    if (current >= runtime.length) return Action.END;
    
    Action action = runtime[current];
    current += 1;
    
    return action;
  }

  @Override
  public String toString() {
    return String.format("%d", this.id);
  }

  public int getId() { return id; }

  public int getRemainingTime() {
    return runtime.length - current;
  }
}
