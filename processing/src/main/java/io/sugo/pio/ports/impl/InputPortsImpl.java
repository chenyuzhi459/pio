package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.MetaData;

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

    @Override
    public InputPort createPort(String name, Class<? extends IOObject> clazz) {
        return createPort(name, new MetaData(clazz));
    }

    @Override
    public InputPort createPort(String name, MetaData metaData) {
        InputPort in = createPort(name);
        return in;
    }

}
