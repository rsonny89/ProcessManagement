package com.rsonny.process;

import java.util.LinkedList;

public interface Scheduler {
  void add(LinkedList<Process> queue, Process process);
  Process schedule(LinkedList<Process> queue, Process current);
}
