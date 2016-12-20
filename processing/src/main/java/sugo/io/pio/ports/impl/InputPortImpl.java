package sugo.io.pio.ports.impl;

import sugo.io.pio.operator.IOObject;
import sugo.io.pio.ports.Port;
import sugo.io.pio.ports.Ports;

/**
 */
public class InputPortImpl extends AbstractInputPort {
    protected InputPortImpl(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    @Override
    public void receive(IOObject object) {
        setData(object);
    }
}
