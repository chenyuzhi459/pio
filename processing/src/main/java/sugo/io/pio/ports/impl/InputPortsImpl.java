package sugo.io.pio.ports.impl;

import sugo.io.pio.ports.InputPort;
import sugo.io.pio.ports.InputPorts;
import sugo.io.pio.ports.PortOwner;

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
