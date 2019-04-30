package com.rsonny.process;

import java.util.LinkedList;

public class InterruptQueue extends LinkedList<Interrupt> {
  @Override
  public boolean add(Interrupt interrupt) {
    for (int i = 0; i < super.size(); i++) {
      Interrupt item = super.get(i);

      if (interrupt.getCycle() < item.getCycle()) {
        super.add(i, interrupt);
        return true;
      }
    }

    super.add(interrupt);

    return true;
  }

  public Interrupt next(int cycle) {
    if (super.isEmpty()) {
      return null;
    }

    if (super.peek().getCycle() <= cycle) {
      return super.remove();
    }

    return null;
  }
}
