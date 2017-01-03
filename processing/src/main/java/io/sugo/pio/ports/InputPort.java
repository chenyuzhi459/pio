package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public interface InputPort extends Port {
    /**
     * Receives data from the output port.
     */
    public void receive(IOObject object);

    /** Does the same as {@link #receive(IOObject)} but only with meta data. */
    public void receiveMD(MetaData metaData);

    // /** Called by the OutputPort when it connects to this InputPort. */
    // public void connect(OutputPort outputPort);

    /** Returns the output port to which this input port is connected. */
    public OutputPort getSource();

    /** Checks all registered preconditions. */
    public void checkPreconditions();

    /** Returns true if the given input is compatible with the preconditions. */
    public boolean isInputCompatible(MetaData input);
}
