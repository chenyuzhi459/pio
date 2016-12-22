package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public abstract class AbstractInputPort extends AbstractPort implements InputPort {
    protected AbstractInputPort(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    private MetaData metaData;

    private MetaData realMetaData;

    /** The port to which this port is connected. */
    private OutputPort sourceOutputPort;

    @Override
    public OutputPort getSource() {
        return sourceOutputPort;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MetaData> T getMetaData(Class<T> desiredClass) {
        if (realMetaData != null) {
            return (T) realMetaData;
        } else {
            if (metaData != null) {
            }
            return (T) metaData;
        }
    }

    public void connect(OutputPort outputPort) {
        this.sourceOutputPort = outputPort;
    }

    @Override
    public boolean isConnected() {
        return sourceOutputPort != null;
    }
}
