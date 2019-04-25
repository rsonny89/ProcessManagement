package com.github.rsonny;

import java.util.LinkedList;

public class ShortestJobScheduler extends Scheduler {
  @Override
  public Process schedule(Process current) {
    if (current == null) {
      if (ready.isEmpty()) return null;

      return ready.remove();
    }

    if (ready.isEmpty()) return current;

    if (current.getRemainingTime() <= ready.peek().getRemainingTime()) {
      return current;
    }

    add(current);

    return ready.remove();
  }

  @Override
  public void add(Process process) {
    if (process == null) return;

    for (int i = 0; i < ready.size(); i++) {
      if (process.getRemainingTime() < ready.get(i).getRemainingTime()) {
        ready.add(i, process);
        return;
      }
    }

    ready.add(process);
  }
}
