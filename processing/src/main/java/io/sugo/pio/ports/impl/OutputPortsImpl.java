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
    public OutputPort createPort(String name, String description) {
        return createPort(name, description, true);
    }

    @Override
    public OutputPort createPort(String name, String description, boolean add) {
        OutputPort out = new OutputPortImpl(this, name, description);
        if (add) {
            addPort(out);
        }
        return out;
    }

    @Override
    public OutputPort createPassThroughPort(String name, String description) {
        OutputPort in = new OutputPortImpl(this, name, description);
        addPort(in);
        return in;
    }

    @Override
    public OutputPort createPassThroughPort(String name) {
        OutputPort in = new OutputPortImpl(this, name, name);
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
