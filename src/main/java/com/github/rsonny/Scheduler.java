package com.github.rsonny;

import java.util.LinkedList;

public interface Scheduler {
  void schedule(LinkedList<Process> queue, Process process);
  Process next(LinkedList<Process> queue, Process current);
}
