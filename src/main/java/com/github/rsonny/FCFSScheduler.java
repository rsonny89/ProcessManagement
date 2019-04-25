package com.github.rsonny;

import java.util.LinkedList;

public class FCFSScheduler implements Scheduler {
  @Override
  public Process schedule(LinkedList<Process> ready, Process current) {
    if (current != null) return current;

    return ready.isEmpty() ? null : ready.remove();
  }
}
