package io.sugo.pio.parameter.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.parameter.ParameterHandler;

/**
 */
public class BooleanParameterCondition extends ParameterCondition {
    @JsonProperty
    private boolean conditionValue;

    public BooleanParameterCondition(ParameterHandler parameterHandler, String conditionParameter, boolean becomeMandatory,
                                     boolean conditionValue) {
        super(parameterHandler, conditionParameter, becomeMandatory);
        this.conditionValue = conditionValue;
    }

    @Override
    public boolean isConditionFullfilled() {
        return parameterHandler.getParameterAsBoolean(conditionParameter) == conditionValue;
    }
}
