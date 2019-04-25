package com.github.rsonny;

import java.util.LinkedList;

public class ShortestTimeScheduler implements Scheduler {
  @Override
  public Process schedule(LinkedList<Process> ready, Process current) {
    Process shortest = current;
    int index = -1;

    for (int i = 0; i < ready.size(); i++) {
      Process process = ready.get(i);

      if (shortest == null) {
        shortest = process;
        continue;
      }

      if (process.getRemainingTime() < shortest.getRemainingTime()) {
        shortest = process;
        index = i;
      }
    }

    return index >= 0 ? ready.remove(index) : current;
  }
}
