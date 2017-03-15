package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.Port;

/**
 */
public class InputPortImpl extends AbstractInputPort {

    protected InputPortImpl(Ports<? extends Port> owner, String name, String description) {
        super(owner, name, description);
    }

    @Override
    public void receive(IOObject object) {
        setData(object);
    }
}
