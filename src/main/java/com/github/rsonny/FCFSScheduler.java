package com.github.rsonny;

public class FCFSScheduler extends Scheduler {
  @Override
  public Process schedule(Process current) {
    if (current != null) return current;

    return ready.isEmpty() ? null : ready.remove();
  }

  @Override
  public void add(Process process) {
    ready.add(process);
  }
}
