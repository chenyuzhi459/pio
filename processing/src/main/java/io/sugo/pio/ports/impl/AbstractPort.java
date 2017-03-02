package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.error.PortUserError;
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

    @JsonProperty
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
            throw new PortUserError(this, "pio.error.operator.no_input", getSpec());
        } else if (desiredClass.isAssignableFrom(data.getClass())) {
            return desiredClass.cast(data);
        } else {
            PortUserError error = new PortUserError(this, "pio.error.operator.wrong_input_type", this.getName());
            error.setExpectedType(desiredClass);
            error.setActualType(data.getClass());
            throw error;
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

    @SuppressWarnings("unchecked")
    @Deprecated
    @Override
    public <T extends IOObject> T getDataOrNull() throws UserError {
        IOObject data = getAnyDataOrNull();
        return (T) data;
    }

    @Override
    public final String getName() {
        return name;
    }

    /** Don't use this method. Use {@link Ports#renamePort(Port,String)}. */
    protected void setName(String newName) {
        this.name = newName;
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
