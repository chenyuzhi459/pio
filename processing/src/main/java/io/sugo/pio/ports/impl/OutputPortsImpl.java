package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.OutputPorts;

/**
 */
public class OutputPortsImpl extends AbstractPorts<OutputPort> implements OutputPorts {
    public OutputPortsImpl(PortOwner owner) {
        super(owner);
    }


    @Override
    public OutputPort createPort(String name) {
        return createPort(name, true);
    }

    @Override
    public OutputPort createPort(String name, boolean add) {
        OutputPort out = new OutputPortImpl(this, name);
        if (add) {
            addPort(out);
        }
        return out;
    }
}
