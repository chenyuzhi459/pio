package io.sugo.pio.ports;

import io.sugo.pio.operator.IOContainer;

import java.util.List;
import java.util.Observer;

/**
 */
public interface Ports<T> {
    /** Creates a new port and adds it to these Ports. */
    public T createPort(String name);

    /** Creates a new port and adds it to these Ports if add is true.. */
    public T createPort(String name, boolean add);

    /**
     * This is a backport method to generate IOContainers containing all output objects of the given
     * ports.
     */
    public IOContainer createIOContainer(boolean onlyConnected, boolean omitNullResults);

    /** Returns an immutable view of the ports. */
    public List<T> getAllPorts();

    /**
     * Add a port and notify the {@link Observer}s.
     */
    public void addPort(T port);

    /** Returns the operator and process to which these ports are attached. */
    public PortOwner getOwner();

    /** Frees memory occupied by references to ioobjects. */
    public void freeMemory();
}
