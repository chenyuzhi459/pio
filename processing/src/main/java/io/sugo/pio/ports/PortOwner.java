package io.sugo.pio.ports;

import io.sugo.pio.operator.Operator;

/**
 */
public interface PortOwner {
    /** Returns the operator to which these ports are attached. */
    public Operator getOperator();
}
