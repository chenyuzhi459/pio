package io.sugo.pio.spark.ports;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import io.sugo.pio.spark.ports.SparkOutputPortImpl;

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
