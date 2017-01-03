package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.util.ReferenceCache;


/**
 */
public abstract class AbstractPort implements Port {
    private PortOwner portOwner;

    @JsonProperty
    private String name;

    private static final ReferenceCache<IOObject> IOO_REFERENCE_CACHE = new ReferenceCache<>(20);
    private ReferenceCache<IOObject>.Reference weakDataReference;

    private IOObject hardDataReference;

    private boolean locked = false;

    public AbstractPort(String name) {
        this.name = name;
    }

    protected final void setData(IOObject object) {
        this.weakDataReference = IOO_REFERENCE_CACHE.newReference(object);
        this.hardDataReference = object;
    }

    @Override
    public PortOwner getPortOwner() {
        return portOwner;
    }

    @Override
    public void setPortOwner(PortOwner portOwner) {
        this.portOwner = portOwner;
    }

    @Override
    public IOObject getAnyDataOrNull() {
        if (hardDataReference != null) {
            return hardDataReference;
        } else {
            // This method is invoked from many places that should not keep the cache entry warm
            // (e.g., visualizations). Thus, perform only a weak get.
            return this.weakDataReference != null ? this.weakDataReference.weakGet() : null;
        }
    }

    @Override
    public <T extends IOObject> T getData(Class<T> desiredClass) {
        IOObject data = getAnyDataOrNull();
        if (data == null) {
            // TODO: Maybe change this to a checked exception
            throw new RuntimeException("");
        } else if (desiredClass.isAssignableFrom(data.getClass())) {
            return desiredClass.cast(data);
        } else {
            // TODO: Maybe change this to a checked exception
            throw new RuntimeException("");
        }
    }

    @Override
    public <T extends IOObject> T getDataOrNull(Class<T> desiredClass) {
        IOObject data = getAnyDataOrNull();
        if (data == null) {
            return null;
        } else if (desiredClass.isAssignableFrom(data.getClass())) {
            return desiredClass.cast(data);
        } else {
            // TODO: Maybe change this to a checked exception
            throw new RuntimeException("");
        }
    }

    @Override
    public final String getName() {
        return name;
    }

//    @Override
//    public Ports<? extends Port> getPorts() {
//        return ports;
//    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void unlock() {
        this.locked = false;
    }

    @Override
    public void lock() {
        this.locked = true;
    }

    /** Releases of the hard reference. */
    @Override
    public void freeMemory() {
        this.hardDataReference = null;
    }
}
