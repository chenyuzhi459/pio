package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.*;

import java.util.List;

/**
 */
public class OutputPortsImpl extends AbstractPorts<OutputPort> implements OutputPorts {
    public OutputPortsImpl(PortOwner owner) {
        super(owner);
    }

    @Override
    public OutputPort createPort(String name) {
        return createPort(name, true);
    }

    @Override
    public OutputPort createPort(PortType type) {
        return createPort(type, true);
    }

    @Override
    public OutputPort createPort(String name, boolean add) {
        OutputPort out = new OutputPortImpl(this, name);
        if (add) {
            addPort(out);
        }
        return out;
    }

    @Override
    public OutputPort createPort(PortType type, boolean add) {
        OutputPort out = new OutputPortImpl(this, type.getName(), type.getDescription());
        if (add) {
            addPort(out);
        }
        return out;
    }

    @Override
    public OutputPort createPassThroughPort(String name) {
        OutputPort in = new OutputPortImpl(this, name);
        addPort(in);
        return in;
    }

    @Override
    public OutputPort createPassThroughPort(PortType type) {
        OutputPort in = new OutputPortImpl(this, type.getName(), type.getDescription());
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
            for (OutputPort port : getAllPorts()) {
                if (port.isConnected()) {
                    InputPort destination = port.getDestination();
                    boolean isException = false;
                    if (exceptions != null) {
                        Operator destOp = destination.getPorts().getOwner().getOperator();
                        if (exceptions.contains(destOp)) {
                            isException = true;
                        }
                    }
                    if (!isException) {
                        port.disconnect();
                        success = true;
                        continue disconnect;
                    }
                }
            }
        } while (success);
    }
}
