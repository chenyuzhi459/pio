package io.sugo.pio.ports.impl;

import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.Ports;

public class OutputPortImpl extends AbstractOutputPort {

    private static final Logger logger = new Logger(OutputPortImpl.class);

    protected OutputPortImpl(Ports<? extends Port> owner, String name) {
        super(owner, name);
    }

    protected OutputPortImpl(Ports<? extends Port> owner, String name, String description) {
        super(owner, name, description);
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
            logger.info("Deliver data from %s[%s] to %s[%s]", getPorts().getOwner().getOperator().getName(),
                    getName(), getDestination().getPorts().getOwner().getOperator().getName(), getDestination().getName());
        }
    }
}
