package io.sugo.pio.ports.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.*;
import io.sugo.pio.tools.AbstractObservable;
import io.sugo.pio.tools.Observable;
import io.sugo.pio.tools.Observer;

import java.util.*;

/**
 */
public abstract class AbstractPorts<T extends Port> extends AbstractObservable<Port> implements Ports<T> {
    private static final Logger logger = new Logger(AbstractPorts.class);

    private final List<T> portList = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, T> portMap = new HashMap<>();
    private boolean portNamesValid = false;
    private String[] portNames;
    private final PortOwner owner;

    private List<PortExtender> portExtenders;

    private final Observer<Port> delegatingObserver = new Observer<Port>() {
        @Override
        public void update(Observable<Port> observable, Port arg) {
            fireUpdate(arg);
        }
    };

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
    public void addPort(T port) {
        if (portMap.containsKey(port.getName())) {
            return;
        }
        portList.add(port);
        portMap.put(port.getName(), port);
        portNamesValid = false;
        port.addObserver(delegatingObserver, false);
        fireUpdate(port);
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
            port.removeObserver(delegatingObserver);
            fireUpdate();
        }
    }

    @Override
    public void removeAll() {
        // don't iterate to avoid concurrent modification
        while (getNumberOfPorts() != 0) {
            removePort(getPortByIndex(0));
        }
    }

    @Override
    public int getNumberOfPorts() {
        return portList.size();
    }

    @Override
    public T getPortByIndex(int index) {
        return portList.get(index);
    }

    @Override
    public T getPortByName(String name) {
        T port = portMap.get(name);
        if (port != null) {
            return port;
        } else {
            // LogService.getRoot().fine("Port '"+name+"' does not exist. Checking for extenders.");
            logger.debug("com.rapidminer.operator.ports.impl.AbstractPorts.port_does_not_exist",
                    name);
            if (portExtenders != null) {
                for (PortExtender extender : portExtenders) {
                    String prefix = extender.getNamePrefix();
                    if (name.startsWith(prefix)) {
                        // LogService.getRoot().fine("Found extender with prefix '"+prefix+"'.
                        // Trying to extend.");
                        logger.debug(
                                "com.rapidminer.operator.ports.impl.AbstractPorts.found_extender", prefix);
                        try {
                            int index = Integer.parseInt(name.substring(prefix.length()));
                            extender.ensureMinimumNumberOfPorts(index); // numbering starts at 1
                            T secondTry = portMap.get(name);
                            if (secondTry == null) {
                                // LogService.getRoot().warning("Port extender "+prefix+" did not
                                // extend to size "+index+".");
                                logger.warn("com.rapidminer.operator.ports.impl.AbstractPorts.port_extender_did_not_extend",
                                        new Object[]{prefix, index});
                            } else {
                                // LogService.getRoot().fine("Port was created. Ports are now:
                                // "+getAllPorts());
                                logger.debug("com.rapidminer.operator.ports.impl.AbstractPorts.ports_created", getAllPorts());
                            }
                            return secondTry;
                        } catch (NumberFormatException e) {
                            // LogService.getRoot().log(Level.WARNING,
                            // "Cannot extend "+prefix+": "+e, e);
                            logger.warn("com.rapidminer.operator.ports.impl.AbstractPorts.extending_error", prefix, e);
                            return null;
                        }
                    }
                }
            }
            // no extender found
            return null;
        }
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
    public PortOwner getOwner() {
        return owner;
    }

    @Override
    public boolean containsPort(T port) {
        return portList.contains(port);
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
    public void renamePortDesc(T port, String newDescription) {
        ((AbstractPort) port).setDescription(newDescription);
    }

    @Override
    public void clear(int clearFlags) {
        for (T port : getAllPorts()) {
            port.clear(clearFlags);
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
    public IOContainer createIOContainer(boolean onlyConnected) {
        return createIOContainer(onlyConnected, true);
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

    @Override
    public int getNumberOfConnectedPorts() {
        int count = 0;
        for (Port port : getAllPorts()) {
            if (port.isConnected()) {
                count++;
            }
        }
        return count;
    }
}
