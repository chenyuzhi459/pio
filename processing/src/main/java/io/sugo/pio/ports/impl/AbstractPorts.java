package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.*;

import java.util.*;

/**
 */
public abstract class AbstractPorts<T extends Port> implements Ports<T> {
    private final List<T> portList = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, T> portMap = new HashMap<>();
    private boolean portNamesValid = false;
    private String[] portNames;
    private final PortOwner owner;

    private List<PortExtender> portExtenders;

    @JsonProperty
    public List<T> getPortList() {
        return portList;
    }

    @Override
    public T getPort(String name) {
        for (T port : portList) {
            if (name.equals(port.getName())) {
                return port;
            }
        }

        return null;
    }

    public AbstractPorts(PortOwner owner) {
        this.owner = owner;
        portNamesValid = false;
    }

    private void updatePortNames() {
        if (!portNamesValid) {
            portNames = new String[portList.size()];
            int i = 0;
            synchronized (portList) {
                for (Port port : portList) {
                    portNames[i++] = port.getName();
                }
            }
            portNamesValid = true;
        }
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
        portNamesValid = false;
    }

    @Override
    public void removePort(T port) throws PortException {
        if (!portList.contains(port) || port.getPorts() != this) {
            throw new PortException("Cannot remove " + port + ".");
        } else {
            if (port.isConnected()) {
                if (port instanceof OutputPort) {
                    ((OutputPort) port).disconnect();
                } else {
                    ((InputPort) port).getSource().disconnect();
                }
            }
            portList.remove(port);
            portMap.remove(port.getName());
        }
    }

    @Override
    public PortOwner getOwner() {
        return owner;
    }

    @Override
    public void renamePort(T port, String newName) {
        if (portMap.containsKey(newName)) {
            throw new PortException("Port name already used: " + port.getName());
        }
        portMap.remove(port.getName());
        ((AbstractPort) port).setName(newName);
        portMap.put(newName, port);
    }

    @Override
    public String[] getPortNames() {
        updatePortNames();
        return portNames;
    }

    @Override
    public List<T> getAllPorts() {
        synchronized (portList) {
            return Collections.unmodifiableList(new ArrayList<>(portList));
        }
    }

    @Override
    public void pushDown(T port) {
        if (!portList.contains(port)) {
            throw new PortException("Cannot push down " + port.getName() + ": port does not belong to " + this);
        }
        portList.remove(port);
        portList.add(port);
    }

    @Override
    public void registerPortExtender(PortExtender extender) {
        if (portExtenders == null) {
            portExtenders = new LinkedList<>();
        }
        portExtenders.add(extender);
    }

    @Override
    public void unlockPortExtenders() {
        if (portExtenders != null) {
            for (PortExtender extender : portExtenders) {
                extender.ensureMinimumNumberOfPorts(0);
            }
        }
    }

    @Override
    public void freeMemory() {
        for (Port inputPort : getAllPorts()) {
            inputPort.freeMemory();
        }
    }
}
