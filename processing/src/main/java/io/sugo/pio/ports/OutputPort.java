package io.sugo.pio.ports;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.impl.OutputPortImpl;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "outputType", defaultImpl = OutputPortImpl.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "default", value = OutputPortImpl.class)
})
public interface OutputPort extends Port {
    /**
     * Connects to an input port.
     *
     *             if already connected.
     */
    void connectTo(InputPort inputPort);

    /**
     * Disconnects the OutputPort from its InputPort. Note: As a side effect, disconnecting ports
     * may trigger PortExtenders removing these ports. In order to avoid this behaviour,
     * {@link #lock()} port first.
     *
     */
    void disconnect();

    /**
     * Delivers an object to the connected {@link InputPort} or ignores it if the output port is not
     * connected.
     */
    void deliver(IOObject object);

    /** Returns the destination input port. */
    InputPort getDestination();

    /**
     * Asks the owning operator
     */
    boolean shouldAutoConnect();
}
