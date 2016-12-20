package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.Port;

/**
 *
 */
public class OutputPortImpl extends AbstractOutputPort {
    protected OutputPortImpl(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    @Override
    public void deliver(IOObject object) {
        // registering history of object
        if (object != null) {
            object.appendOperatorToHistory(getPorts().getOwner().getOperator(), this);

            // set source if not yet set
            if (object.getSource() == null) {
                if (getPorts().getOwner().getOperator() != null) {
                    object.setSource(getPorts().getOwner().getOperator().getName());
                }
            }
        }

        // delivering data
        setData(object);
        if (isConnected()) {
            getDestination().receive(object);
        }
    }
}
