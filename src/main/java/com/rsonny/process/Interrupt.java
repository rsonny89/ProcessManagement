package com.rsonny.process;

/**
 * Representation of a CPU interrupt.
 */
public class Interrupt {
  /**
   * Representation of the type of interrupt.
   */
  public enum Type {
    NEW_PROCESS("NEW"), // Represents a new process.
    IO_FINISHED("IO");  // Represents when an IO call is complete.

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

  /**
   * Creates a new interrupt.
   * @param type Type of interrupt this should be.
   * @param cycle Target cycle for the interrupt to occur.
   * @param process Process to associated with the interrupt.
   */
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
