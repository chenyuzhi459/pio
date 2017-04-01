package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.error.MissingIOObjectException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

    public IOContainer addIoObject(IOObject ioObject) {
        if (!ioObjects.contains(ioObject)) {
            ioObjects.add(ioObject);
        }

        return this;
    }

    public IOContainer addIoObjects(List<IOObject> ioObjects) {
        ioObjects.forEach(ioObject -> {
            addIoObject(ioObject);
        });

        return this;
    }

    /** Gets the first IOObject which is of class cls. */
    public <T extends IOObject> T get(Class<T> cls) throws MissingIOObjectException {
        return getInput(cls, 0, false);
    }

    /**
     * Gets the nr-th IOObject which is of class cls. If remove is set to true, the object is
     * afterwards removed from this IOContainer.
     */
    private <T extends IOObject> T getInput(Class<T> cls, int nr, boolean remove) throws MissingIOObjectException {
        int n = 0;
        Iterator<IOObject> i = ioObjects.iterator();
        while (i.hasNext()) {
            IOObject object = i.next();
            if ((object != null) && (cls.isInstance(object))) {
                if (n == nr) {
                    if (remove) {
                        i.remove();
                    }
                    return cls.cast(object);
                } else {
                    n++;
                }
            }
        }
        throw new MissingIOObjectException(cls);
    }
}
