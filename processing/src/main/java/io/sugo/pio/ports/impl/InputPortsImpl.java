package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.metadata.MetaData;

import java.util.List;

/**
 */
public class InputPortsImpl extends AbstractPorts<InputPort> implements InputPorts {

    public InputPortsImpl(PortOwner owner) {
        super(owner);
    }

    @Override
    public void checkPreconditions() {
        for (InputPort port : getAllPorts()) {
            port.checkPreconditions();
        }
    }

    @Override
    public InputPort createPort(String name) {
        return createPort(name, true);
    }

    @Override
    public InputPort createPort(PortType type) {
        return createPort(type,true);
    }

    @Override
    public InputPort createPort(String name, boolean add) {
        InputPort in = new InputPortImpl(this, name);
        if (add) {
            addPort(in);
        }
        return in;
    }

    @Override
    public InputPort createPort(PortType type, boolean add) {
        InputPort in = new InputPortImpl(this, type.getName(), type.getDescription());
        if (add) {
            addPort(in);
        }
        return in;
    }

    @Override
    public InputPort createPort(String name, Class<? extends IOObject> clazz) {
        return createPort(name, new MetaData(clazz));
    }

    @Override
    public InputPort createPort(PortType type, Class<? extends IOObject> clazz) {
        return createPort(type, new MetaData(clazz));
    }

    @Override
    public InputPort createPort(String name, MetaData metaData) {
        InputPort in = createPort(name);
        return in;
    }

    @Override
    public InputPort createPort(PortType type, MetaData metaData) {
        InputPort in = createPort(type);
        return in;
    }

    @Override
    public InputPort createPassThroughPort(String name) {
        InputPort in = new InputPortImpl(this, name);
        addPort(in);
        return in;
    }

    @Override
    public InputPort createPassThroughPort(PortType type) {
        InputPort in = new InputPortImpl(this, type.getName(), type.getDescription());
        addPort(in);
        return in;
    }

    @Override
    public void disconnectAll() {
        disconnectAllBut(null);
    }

    @Override
    public void disconnectAllBut(List<Operator> exceptions) {
        boolean success;
        disconnect: do {
            success = false;
            for (InputPort port : getAllPorts()) {
                if (port.isConnected()) {
                    OutputPort source = port.getSource();
                    boolean isException = false;
                    if (exceptions != null) {
                        Operator sourceOp = source.getPorts().getOwner().getOperator();
                        if (exceptions.contains(sourceOp)) {
                            isException = true;
                        }
                    }
                    if (!isException) {
                        source.disconnect();
                        success = true;
                        continue disconnect;
                    }
                }
            }
        } while (success);
    }

}
