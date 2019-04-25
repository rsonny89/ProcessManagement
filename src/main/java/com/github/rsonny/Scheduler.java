package com.github.rsonny;

import java.util.LinkedList;

//public interface Scheduler {
//  void add(LinkedList<Process> ready, Process process);
//  Process schedule(LinkedList<Process> ready, Process current);
//}

public abstract class Scheduler {
  LinkedList<Process> ready = new LinkedList<>();

  public abstract Process schedule(Process current);

  public abstract void add(Process process);

  public boolean isEmpty() {
    return ready.isEmpty();
  }

  public void print(Process current, Process cached) {
    System.out.println();
    System.out.println("[INFO] =================");
    System.out.println("Order PID Remaining");

    if (current == null && cached == null) {
      System.out.println("    - -   -");
    }

    if (current != null ) {
      System.out.printf("    - %-3s %d\n", current, current.getRemainingTime());
    }

    if (cached != null) {
      System.out.printf("    - %-3s %d\n", cached, cached.getRemainingTime());
    }

    for (int i = 0; i < ready.size(); i++) {
      Process process = ready.get(i);

      System.out.printf("%5d %-3s %d\n", i, process, process.getRemainingTime());
    }

    System.out.println("========================");
  }
}
