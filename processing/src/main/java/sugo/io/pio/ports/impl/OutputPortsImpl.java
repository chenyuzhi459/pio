package sugo.io.pio.ports.impl;

import sugo.io.pio.ports.OutputPort;
import sugo.io.pio.ports.OutputPorts;
import sugo.io.pio.ports.PortOwner;

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
