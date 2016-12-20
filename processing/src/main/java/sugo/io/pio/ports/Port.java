package sugo.io.pio.ports;

import sugo.io.pio.operator.IOObject;

/**
 */
public interface Port {
    public static final int CLEAR_META_DATA_ERRORS = 1 << 0;
    public static final int CLEAR_METADATA = 1 << 1;
    public static final int CLEAR_DATA = 1 << 2;
    public static final int CLEAR_SIMPLE_ERRORS = 1 << 3;
    public static final int CLEAR_REAL_METADATA = 1 << 4;
    public static final int CLEAR_ALL = CLEAR_META_DATA_ERRORS | CLEAR_METADATA | CLEAR_DATA | CLEAR_SIMPLE_ERRORS
            | CLEAR_REAL_METADATA;


    /** A human readable, unique (operator scope) name for the port. */
    public String getName();

    /** Returns true if connected to another Port. */
    public boolean isConnected();

    /**
     * Returns the last object delivered to the connected {@link InputPort} or received from the
     * connected {@link OutputPort}. Never throws an exception.
     */
    public IOObject getAnyDataOrNull();

    /** Returns the set of ports to which this port belongs. */
    public Ports<? extends Port> getPorts();

    /**
     * Locks the port so port extenders do not remove the port if disconnected. unlocks it.
     */
    public void lock();

    /**
     * @see #lock()
     */
    public void unlock();

    /**
     * @see #lock()
     */
    public boolean isLocked();

    /** Releases of any hard reference to IOObjects held by this class. */
    void freeMemory();
}
