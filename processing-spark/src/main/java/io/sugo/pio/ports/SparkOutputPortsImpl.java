package io.sugo.pio.ports;

import io.sugo.pio.ports.impl.OutputPortsImpl;

/**
 */
public class SparkOutputPortsImpl extends OutputPortsImpl {
    public SparkOutputPortsImpl(PortOwner owner) {
        super(owner);
    }

    public OutputPort createPort(String name, boolean add) {
        SparkOutputPortImpl out = new SparkOutputPortImpl(this, name);
        if(add) {
            this.addPort(out);
        }

        return out;
    }

    public OutputPort createPassThroughPort(String name) {
        SparkOutputPortImpl in = new SparkOutputPortImpl(this, name);
        this.addPort(in);
        return in;
    }
}
