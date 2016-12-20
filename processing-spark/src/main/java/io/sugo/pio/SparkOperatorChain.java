package io.sugo.pio;

import io.sugo.pio.operator.OperatorChain;
import io.sugo.pio.operator.OperatorDescription;

/**
 */
public abstract class SparkOperatorChain extends OperatorChain implements KillableOperation {
    public SparkOperatorChain(OperatorDescription description, String... subprocessNames) {
        super(description, subprocessNames);
    }
}
