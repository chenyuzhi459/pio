package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.Ports;
import io.sugo.pio.ports.metadata.MetaDataError;
import io.sugo.pio.util.ReferenceCache;

import java.util.LinkedList;
import java.util.List;

/**
 */
public abstract class AbstractPort implements Port {

    private final List<MetaDataError> errorList = new LinkedList<>();
    private final Ports<? extends Port> ports;

    private String name;

    private static final ReferenceCache<IOObject> IOO_REFERENCE_CACHE = new ReferenceCache<>(20);
    private ReferenceCache<IOObject>.Reference weakDataReference;

    private IOObject hardDataReference;

    private boolean locked = false;

    public AbstractPort(Ports<? extends Port> owner, String name) {
        this.name = name;
        this.ports = owner;
    }

    protected final void setData(IOObject object) {
        this.weakDataReference = IOO_REFERENCE_CACHE.newReference(object);
        this.hardDataReference = object;
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

    @Override
    public void addError(MetaDataError metaDataError) {
        errorList.add(metaDataError);
    }

    @Override
    public Ports<? extends Port> getPorts() {
        return ports;
    }

    @Override
    public String getSpec() {
        if (getPorts() != null) {
            return getPorts().getOwner().getOperator().getName() + "." + getName();
        } else {
            return "DUMMY." + getName();
        }
    }

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
