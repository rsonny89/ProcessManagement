package com.rsonny.process;

public class Interrupt {
  public enum Type {
    NEW_PROCESS("NEW"), IO_FINISHED("IO");

    private String name;

    private Type(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  private Type type;
  private Process process;
  private int cycle;

  public Interrupt(Type type, int cycle, Process process) {
    this.type = type;
    this.cycle = cycle;
    this.process = process;
  }

  public int getCycle() {
    return cycle;
  }

  public Process getProcess() {
    return process;
  }

  public Type getType() {
    return type;
  }
}
