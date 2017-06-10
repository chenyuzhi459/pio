package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.metadata.CompatibilityLevel;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.Precondition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

/**
 */
public abstract class AbstractInputPort extends AbstractPort implements InputPort {

    protected AbstractInputPort(Ports<? extends Port> owner, String name, String description) {
        super(owner, name, description);
    }

    private final Collection<Precondition> preconditions = new LinkedList<>();

    private MetaData metaData;

    /** The port to which this port is connected. */
    private OutputPort sourceOutputPort;

    @Override
    public void receiveMD(MetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public OutputPort getSource() {
        return sourceOutputPort;
    }

    @Override
    public void checkPreconditions() {
        MetaData metaData = getMetaData();
        for (Precondition precondition : preconditions) {
            try {
                precondition.check(metaData);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MetaData> T getMetaData(Class<T> desiredClass) {
        if (metaData != null) {
        }
        return (T) metaData;
    }

    public void connect(OutputPort outputPort) {
        this.sourceOutputPort = outputPort;
        fireUpdate(this);
    }


    @Override
    public boolean isInputCompatible(MetaData input, CompatibilityLevel level) {
        for (Precondition precondition : preconditions) {
            if (!precondition.isCompatible(input, level)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isConnected() {
        return sourceOutputPort != null;
    }

    @Override
    public void addPrecondition(Precondition precondition) {
        preconditions.add(precondition);

    }

    @Override
    public Collection<Precondition> getAllPreconditions() {
        return Collections.unmodifiableCollection(preconditions);
    }

}
