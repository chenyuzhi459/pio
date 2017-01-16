package io.sugo.pio.ports;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.impl.InputPortImpl;
import io.sugo.pio.ports.metadata.CompatibilityLevel;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.Precondition;

import java.util.Collection;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "inputType", defaultImpl = InputPortImpl.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "default", value = InputPortImpl.class)
})
public interface InputPort extends Port {
    /**
     * Receives data from the output port.
     */
    void receive(IOObject object);

    /** Does the same as {@link #receive(IOObject)} but only with meta data. */
    public void receiveMD(MetaData metaData);

    // /** Called by the OutputPort when it connects to this InputPort. */
    // public void connect(OutputPort outputPort);

    /**
     * Returns the output port to which this input port is connected.
     */
    OutputPort getSource();

    /** Adds a precondition to this input port. */
    public void addPrecondition(Precondition precondition);

    /** Returns a collection (view) of all preconditions assigned to this InputPort. */
    public Collection<Precondition> getAllPreconditions();

    /** Checks all registered preconditions. */
    public void checkPreconditions();

    /** Returns true if the given input is compatible with the preconditions. */
    public boolean isInputCompatible(MetaData input, CompatibilityLevel level);
}
