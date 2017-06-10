package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.*;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public abstract class AbstractOutputPort extends AbstractPort implements OutputPort {

    protected AbstractOutputPort(Ports<? extends Port> owner, String name, String description) {
        super(owner, name, description);
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

    protected void assertConnected() throws PortException {
        if (this.connectedTo == null) {
            throw new PortException(this, "Not connected.");
        }
    }

    @Override
    public void connectTo(InputPort inputPort) {
        if (this.connectedTo == inputPort) {
            return;
        }

        this.connectedTo = inputPort;
        ((AbstractInputPort) inputPort).connect(this);
        fireUpdate(this);
    }

    @Override
    public void disconnect() {
        assertConnected();
        ((AbstractInputPort) this.connectedTo).connect(null);
        this.connectedTo.receive(null);
        this.connectedTo.receiveMD(null);
        this.connectedTo = null;
        fireUpdate(this);
    }
}
