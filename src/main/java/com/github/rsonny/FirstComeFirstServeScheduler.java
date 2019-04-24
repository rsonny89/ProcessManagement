package com.github.rsonny;

import java.util.LinkedList;

public class FirstComeFirstServeScheduler implements Scheduler {
  public void schedule(LinkedList<Process> queue, Process process) {
    queue.add(process);
  }
  public Process next(LinkedList<Process> queue, Process current) {
    if (current == null) return queue.isEmpty() ? null : queue.remove();

    return current;
  }
}
