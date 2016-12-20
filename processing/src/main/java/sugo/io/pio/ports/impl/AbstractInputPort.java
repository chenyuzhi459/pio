package sugo.io.pio.ports.impl;

import sugo.io.pio.ports.InputPort;
import sugo.io.pio.ports.OutputPort;
import sugo.io.pio.ports.Port;
import sugo.io.pio.ports.Ports;

/**
 */
public abstract class AbstractInputPort extends AbstractPort implements InputPort {
    protected AbstractInputPort(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    /** The port to which this port is connected. */
    private OutputPort sourceOutputPort;

    @Override
    public OutputPort getSource() {
        return sourceOutputPort;
    }

    public void connect(OutputPort outputPort) {
        this.sourceOutputPort = outputPort;
    }

    @Override
    public boolean isConnected() {
        return sourceOutputPort != null;
    }
}
