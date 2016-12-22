package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.metadata.MetaData;

/**
 */
public abstract class AbstractOutputPort extends AbstractPort implements OutputPort {
    protected AbstractOutputPort(Ports<? extends Port> owner, String name) {
        super(owner, name);
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

    /*
	 * private void assertDisconnected() throws PortException { if (this.connectedTo != null) {
	 * throw new PortException(this, "Already connected."); } }
	 */

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
