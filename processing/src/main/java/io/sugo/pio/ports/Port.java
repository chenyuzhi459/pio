package io.sugo.pio.ports;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.MetaDataError;

import java.io.Serializable;

/**
 */
public interface Port extends Serializable {
    public static final int CLEAR_META_DATA_ERRORS = 1 << 0;
    public static final int CLEAR_METADATA = 1 << 1;
    public static final int CLEAR_DATA = 1 << 2;
    public static final int CLEAR_SIMPLE_ERRORS = 1 << 3;
    public static final int CLEAR_REAL_METADATA = 1 << 4;
    public static final int CLEAR_ALL = CLEAR_META_DATA_ERRORS | CLEAR_METADATA | CLEAR_DATA | CLEAR_SIMPLE_ERRORS
            | CLEAR_REAL_METADATA;


    /**
     * A human readable, unique (operator scope) name for the port.
     */
    String getName();

    /**
     * Returns true if connected to another Port.
     */
    boolean isConnected();

    /**
     * Report an error in the current process setup.
     */
    public void addError(MetaDataError metaDataError);

    /**
     * Returns the meta data currently assigned to this port.
     *
     */
    MetaData getMetaData();

    /**
     * Returns the meta data of the desired class or throws an UserError if available meta data
     * cannot be cast to the desired class. If no meta data is present at all, <code>null</code> is
     * returned.
     */
    <T extends MetaData> T getMetaData(Class<T> desiredClass);

    /**
     * This method returns the object of the desired class or throws an UserError if no object is
     * present or cannot be casted to the desiredClass. * @throws UserError if data is missing or of
     * wrong class.
     */
    <T extends IOObject> T getData(Class<T> desiredClass);

    /**
     * Returns the last object delivered to the connected {@link InputPort} or received from the
     * connected {@link OutputPort}
     */
    <T extends IOObject> T getDataOrNull(Class<T> desiredClass);

    /**
     * Returns the last object delivered to the connected {@link InputPort} or received from the
     * connected {@link OutputPort}
     *
     * @throws UserError If data is not of the requested type.
     * @deprecated call {@link #getDataOrNull(Class)}
     */
    public <T extends IOObject> T getDataOrNull() throws UserError;

    /**
     * Returns the last object delivered to the connected {@link InputPort} or received from the
     * connected {@link OutputPort}. Never throws an exception.
     */
    IOObject getAnyDataOrNull();

    /**
     * Returns the set of ports to which this port belongs.
     */
    Ports<? extends Port> getPorts();

    /**
     * Returns the string "OperatorName.PortName".
     */
    public String getSpec();

    /**
     * Locks the port so port extenders do not remove the port if disconnected. unlocks it.
     */
    void lock();

    /**
     * @see #lock()
     */
    void unlock();

    /**
     * @see #lock()
     */
    boolean isLocked();

    /**
     * Releases of any hard reference to IOObjects held by this class.
     */
    void freeMemory();
}
