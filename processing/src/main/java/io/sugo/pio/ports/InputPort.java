package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public interface InputPort extends Port {
    /**
     * Receives data from the output port.
     */
    void receive(IOObject object);

    // /** Called by the OutputPort when it connects to this InputPort. */
    // public void connect(OutputPort outputPort);

    /**
     * Returns the output port to which this input port is connected.
     */
    OutputPort getSource();

    /**
     * Checks all registered preconditions.
     */
    void checkPreconditions();

    /**
     * Returns true if the given input is compatible with the preconditions.
     */
    boolean isInputCompatible(MetaData input);
}
