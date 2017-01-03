package io.sugo.pio.spark;

import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;

/**
 */
public abstract class SparkOperatorChain extends OperatorChain implements KillableOperation {
    public SparkOperatorChain(String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts) {
        super(name, inputPorts, outputPorts);
    }
}
