package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
public class IOContainer implements Serializable {
    @JsonProperty
    private final List<IOObject> ioObjects;

    public IOContainer(Collection<? extends IOObject> objectCollection) {
        ioObjects = new ArrayList<>(objectCollection.size());
        ioObjects.addAll(objectCollection);
    }

    public List<IOObject> getIoObjects() {
        return ioObjects;
    }
}
