package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;

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

    /** Returns the destination input port. */
    public InputPort getDestination();
}
