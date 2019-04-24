package com.github.rsonny;

import java.util.LinkedList;

public interface Scheduler {
  void schedule(LinkedList<Process> ready, Process process);
}
