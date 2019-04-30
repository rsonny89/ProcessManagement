package com.rsonny.process;

import java.util.LinkedList;

public class ShortestJobScheduler implements Scheduler {
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
