package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOObject;

public class OutputPortImpl extends AbstractOutputPort {
    @JsonCreator
    public OutputPortImpl(
            @JsonProperty("name") String name
    ) {
        super(name);
    }

    @Override
    public void deliver(IOObject object) {
        // registering history of object
        if (object != null) {
            object.appendOperatorToHistory(getPortOwner().getOperator(), this);

            // set source if not yet set
            if (object.getSource() == null) {
                if (getPortOwner().getOperator() != null) {
                    object.setSource(getPortOwner().getOperator().getName());
                }
            }
        }

        // delivering data
        setData(object);
        if (isConnected()) {
            getDestination().receive(object);
        }
        System.out.println(String.format("deliver data from %s to %s", getName(), getDestination().getName()));
    }
}
