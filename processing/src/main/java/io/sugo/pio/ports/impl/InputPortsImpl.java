package io.sugo.pio.ports.impl;

import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.InputPort;

/**
 */
public class InputPortsImpl extends AbstractPorts<InputPort> implements InputPorts {

    public InputPortsImpl(PortOwner owner) {
        super(owner);
    }

    @Override
    public InputPort createPort(String name) {
        return createPort(name, true);
    }

    @Override
    public InputPort createPort(String name, boolean add) {
        InputPort in = new InputPortImpl(this, name);
        if (add) {
            addPort(in);
        }
        return in;
    }

}
