package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.*;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public abstract class AbstractOutputPort extends AbstractPort implements OutputPort {
    protected AbstractOutputPort(String name) {
        super(name);
    }

    private InputPort connectedTo;

    private MetaData metaData;

    @Override
    public void deliverMD(MetaData md) {
        this.metaData = md;
        if (connectedTo != null) {
            this.connectedTo.receiveMD(md);
        }
    }

    @Override
    public InputPort getDestination() {
        return connectedTo;
    }

    @Override
    public boolean isConnected() {
        return connectedTo != null;
    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MetaData> T getMetaData(Class<T> desiredClass){
        if (metaData != null) {
        }
        return (T) metaData;
    }

    @Override
    public void connectTo(InputPort inputPort) {
        if (this.connectedTo == inputPort) {
            return;
        }

        this.connectedTo = inputPort;
        ((AbstractInputPort) inputPort).connect(this);
    }
}
