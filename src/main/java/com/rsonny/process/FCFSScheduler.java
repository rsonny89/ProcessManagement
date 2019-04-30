package com.rsonny.process;

import java.util.LinkedList;

public class FCFSScheduler implements Scheduler {
  public Process schedule(LinkedList<Process> queue, Process current) {
    if (current != null) return current;

    return queue.isEmpty() ? null : queue.remove();
  }

  public void add(LinkedList<Process> queue, Process process) {
    queue.add(process);
  }
}
