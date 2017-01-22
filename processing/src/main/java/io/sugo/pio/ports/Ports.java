package io.sugo.pio.ports;

import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;

import java.io.Serializable;
import java.util.List;
import java.util.Observer;

/**
 */
public interface Ports<T> extends Serializable {
    /** Creates a new port and adds it to these Ports. */
    public T createPort(String name);

    /** Creates a new port and adds it to these Ports if add is true.. */
    public T createPort(String name, boolean add);

    /** Creates (and adds) a new port whose {@link Port#simulatesStack()} returns false. */
    public T createPassThroughPort(String name);

    /**
     * This is a backport method to generate IOContainers containing all output objects of the given
     * ports.
     */
    public IOContainer createIOContainer(boolean onlyConnected, boolean omitNullResults);

    /** Disconnects all ports. */
    public void disconnectAll();

    /**
     * Disconnects all ports with exception to those connections to operators in the given list.
     */
    public void disconnectAllBut(List<Operator> exception);

    /** Returns an immutable view of the ports. */
    public List<T> getAllPorts();

    /**
     * Add a port and notify the {@link Observer}s.
     */
    public void addPort(T port);

    /**
     * Remove a port and notify the {@link Observer}s.
     *
     * @throws PortException
     *             if port is not registered with this Ports instance.
     */
    public void removePort(T port) throws PortException;

    /** Re-adds this port as the last port in this collection. */
    public void pushDown(T port);

    /** Returns the operator and process to which these ports are attached. */
    public PortOwner getOwner();

    /** Renames the given port. */
    public void renamePort(T port, String newName);

    /** Registers a port extender with this ports. */
    public void registerPortExtender(PortExtender extender);

    /** While parsing the process XML file, we may have called loading */
    public void unlockPortExtenders();

    /** Frees memory occupied by references to ioobjects. */
    public void freeMemory();
}
