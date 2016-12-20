package sugo.io.pio.ports;

import sugo.io.pio.operator.Operator;

/**
 */
public interface PortOwner {
    /** Returns the operator to which these ports are attached. */
    public Operator getOperator();
}
