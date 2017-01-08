package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOObject;

/**
 */
public class InputPortImpl extends AbstractInputPort {

    @JsonCreator
    public InputPortImpl(
            @JsonProperty("name") String name
    ) {
        super(name);
    }

    @Override
    public void receive(IOObject object) {
        System.out.println(String.format("receive data from %s to %s", getSource().getName(), getName()));
        setData(object);
    }
}
