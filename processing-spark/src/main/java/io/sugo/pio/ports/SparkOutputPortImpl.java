package io.sugo.pio.ports;

import io.sugo.pio.ports.impl.OutputPortImpl;

/**
 */
public class SparkOutputPortImpl extends OutputPortImpl {
    public SparkOutputPortImpl(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    @Override
    public void connectTo(InputPort inputPort) {
        if(this.getDestination() != inputPort) {
            super.connectTo(inputPort);
        }
    }
}
