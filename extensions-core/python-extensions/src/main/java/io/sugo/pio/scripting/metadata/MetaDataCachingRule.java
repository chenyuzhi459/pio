package io.sugo.pio.scripting.metadata;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.ports.metadata.MDTransformationRule;

/**
 */
public class MetaDataCachingRule implements MDTransformationRule {
    private final Operator operator;

    public MetaDataCachingRule(Operator operator) {
        this.operator = operator;
    }

    @Override
    public void transformMD() {

    }
}
