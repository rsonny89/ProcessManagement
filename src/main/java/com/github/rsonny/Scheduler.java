package com.github.rsonny;

import java.util.LinkedList;

public interface Scheduler {
  Process schedule(LinkedList<Process> ready, Process current);
}
