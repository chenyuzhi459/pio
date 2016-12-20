package sugo.io.pio.ports.impl;

import sugo.io.pio.ports.InputPort;
import sugo.io.pio.ports.OutputPort;
import sugo.io.pio.ports.Port;
import sugo.io.pio.ports.Ports;

/**
 */
public abstract class AbstractOutputPort extends AbstractPort implements OutputPort {
    protected AbstractOutputPort(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    private InputPort connectedTo;

    @Override
    public InputPort getDestination() {
        return connectedTo;
    }

    @Override
    public boolean isConnected() {
        return connectedTo != null;
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
