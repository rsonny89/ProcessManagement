package com.github.rsonny;


import java.util.concurrent.ThreadLocalRandom;

public class Process {

  // Array of actions that will be performed on each CPU cycle spent on
  // performing this process.
  private ProcessAction[] runtime;

  // Number of CPU cycles this process has run.
  private int current = 0;

  // Unique process ID.
  private int id;

  public Process(int id, int actions, int io) {
    this.id = id;
    this.runtime = new ProcessAction[actions];

    for (int i = 0; i < actions; i++) {
      this.runtime[i] = ProcessAction.PROCESS;
    }

    int lastIO = 0;

    for (int i = 0; i < io; i++) {
      lastIO = ThreadLocalRandom.current().nextInt(lastIO, actions);
      if (lastIO != actions - 1) {
        this.runtime[lastIO] = ProcessAction.IO;
      }
    }
  }

  public ProcessAction process() {
    if (current >= runtime.length) return ProcessAction.END;
    
    ProcessAction action = runtime[current];
    current += 1;
    
    return action;
  }

  public int getId() { return id; }
}
