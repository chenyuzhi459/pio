package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.ExecutionUnit;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.Port;

/**
 * Transforms the meta data by applying the meta data transformer of a subprocess. Remember to add
 * this rule at the correct place, i.e. after the rules that ensure that the inner sources receive
 * their meta data.
 *
 * @author Simon Fischer
 *
 */
public class SubprocessTransformRule implements MDTransformationRule {

    private final ExecutionUnit subprocess;

    public SubprocessTransformRule(ExecutionUnit subprocess) {
        this.subprocess = subprocess;
    }

    @Override
    public void transformMD() {
        subprocess.transformMetaData();
    }
}
