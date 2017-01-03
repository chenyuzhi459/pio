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

    private MetaData realMetaData;

    @Override
    public InputPort getDestination() {
        return connectedTo;
    }

    @Override
    public boolean isConnected() {
        return connectedTo != null;
    }

    @Override
    public boolean shouldAutoConnect() {
        return getPortOwner().getOperator().shouldAutoConnect(this);
    }

    @Override
    public MetaData getMetaData() {
        if (realMetaData != null) {
            return realMetaData;
        } else {
            return metaData;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends MetaData> T getMetaData(Class<T> desiredClass){
        if (realMetaData != null) {
            return (T) realMetaData;
        } else {
            if (metaData != null) {
            }
            return (T) metaData;
        }
    }

    @Override
    public void connectTo(InputPort inputPort) {
        if (this.connectedTo == inputPort) {
            return;
        }

        this.connectedTo = inputPort;
        ((AbstractInputPort) inputPort).connect(this);
    }

    @Override
    public void disconnect() {
        ((AbstractInputPort) this.connectedTo).connect(null);
        this.connectedTo.receive(null);
        this.connectedTo = null;
    }

}
