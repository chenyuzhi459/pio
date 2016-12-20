package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;

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
