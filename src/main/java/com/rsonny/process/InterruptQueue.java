package com.rsonny.process;

import java.util.LinkedList;

/**
 * Subclass of a LinkedList that adds handy helper overrides to reduce
 * boilerplate logic.
 */
public class InterruptQueue extends LinkedList<Interrupt> {

  /**
   * Adds and sorts a new interrupt into the list. Items are sorted from lowest
   * to highest target cycle.
   * @param interrupt Interrupt to add.
   * @return If the item was successfully added.
   */
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

  /**
   * Returns the next item on the interrupt queue if one exists for the
   * given cycle.
   * @param cycle Cycle to check for scheduled interrupts.
   * @return Interrupt or null.
   */
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
