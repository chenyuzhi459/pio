package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.LinkedList;

/**
 */
public class MDTransformer {
    private final LinkedList<MDTransformationRule> transformationRules = new LinkedList<MDTransformationRule>();
    private final Operator operator;

    public MDTransformer(Operator op) {
        this.operator = op;
    }

    /** Executes all rules added by {@link #addRule}. */
    public void transformMetaData() {
        for (MDTransformationRule rule : transformationRules) {
            try {
                rule.transformMD();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addRule(MDTransformationRule rule) {
        transformationRules.add(rule);
    }

    /** Convenience method to generate a {@link PassThroughRule}. */
    public void addPassThroughRule(InputPort input, OutputPort output) {
        addRule(new PassThroughRule(input, output, false));
    }

}
