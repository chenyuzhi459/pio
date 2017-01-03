package io.sugo.pio.ports;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.metadata.MetaData;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "inputType", defaultImpl = InputPortImpl.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "default", value = InputPortImpl.class)
})
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
