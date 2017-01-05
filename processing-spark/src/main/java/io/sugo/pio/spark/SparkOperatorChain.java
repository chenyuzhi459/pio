package io.sugo.pio.spark;

import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;
import java.util.List;

/**
 */
public abstract class SparkOperatorChain extends OperatorChain implements KillableOperation {
    public SparkOperatorChain(List<Connection> connections, List<ExecutionUnit> execUnits, String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
        super(connections, execUnits, name, inputPorts, outputPorts);
    }
}
