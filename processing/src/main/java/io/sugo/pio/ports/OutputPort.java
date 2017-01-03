package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.metadata.MDTransformer;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public interface OutputPort extends Port {
    /**
     * Connects to an input port.
     *
     *             if already connected.
     */
    public void connectTo(InputPort inputPort);

    /**
     * Disconnects the OutputPort from its InputPort. Note: As a side effect, disconnecting ports
     * may trigger PortExtenders removing these ports. In order to avoid this behaviour,
     * {@link #lock()} port first.
     *
     */
    public void disconnect();

    /**
     * Delivers an object to the connected {@link InputPort} or ignores it if the output port is not
     * connected.
     */
    public void deliver(IOObject object);

    /**
     * Does the same as {@link #deliver(IOObject)}  except that only meta data is delivered. This
     * method is called by the Operator's {@link MDTransformer}.
     */
    public void deliverMD(MetaData md);

    /** Returns the destination input port. */
    public InputPort getDestination();

    /**
     * Asks the owning operator
     */
    public boolean shouldAutoConnect();
}
