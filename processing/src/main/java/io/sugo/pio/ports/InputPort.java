package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;

/**
 */
public interface InputPort extends Port {
    /**
     * Receives data from the output port.
     */
    public void receive(IOObject object);

    // /** Called by the OutputPort when it connects to this InputPort. */
    // public void connect(OutputPort outputPort);

    /** Returns the output port to which this input port is connected. */
    public OutputPort getSource();
}
