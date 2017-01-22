package io.sugo.pio.ports.impl;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.OutputPorts;

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
    public OutputPort createPort(String name, boolean add) {
        OutputPort out = new OutputPortImpl(this, name);
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
