package sugo.io.pio.ports.impl;

import sugo.io.pio.operator.IOObject;
import sugo.io.pio.ports.Port;
import sugo.io.pio.ports.Ports;

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
