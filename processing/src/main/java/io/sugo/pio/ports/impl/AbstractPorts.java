package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.Ports;

import java.util.*;

/**
 */
public abstract class AbstractPorts<T extends Port> implements Ports<T> {
    private final List<T> portList = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, T> portMap = new HashMap<>();

    private final PortOwner owner;

    public List<T> getPortList() {
        return portList;
    }

    public AbstractPorts(PortOwner owner) {
        this.owner = owner;
    }

    @Override
    public IOContainer createIOContainer(boolean onlyConnected, boolean omitEmptyResults) {
        Collection<IOObject> output = new LinkedList<>();
        for (Port port : getAllPorts()) {
            if (!onlyConnected || port.isConnected()) {
                IOObject data = port.getAnyDataOrNull();
                if (omitEmptyResults) {
                    if (data != null) {
                        output.add(data);
                    }
                } else {
                    output.add(data);
                }
            }
        }
        return new IOContainer(output);
    }

    @Override
    public void addPort(T port) {
        if (portMap.containsKey(port.getName())) {
            return;
        }
        portList.add(port);
        portMap.put(port.getName(), port);
    }

    @Override
    public PortOwner getOwner() {
        return owner;
    }

    @Override
    public List<T> getAllPorts() {
        synchronized (portList) {
            return Collections.unmodifiableList(new ArrayList<>(portList));
        }
    }

    @Override
    public void freeMemory() {
        for (Port inputPort : getAllPorts()) {
            inputPort.freeMemory();
        }
    }
}
