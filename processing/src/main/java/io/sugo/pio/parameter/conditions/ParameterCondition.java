package io.sugo.pio.parameter.conditions;

import io.sugo.pio.parameter.ParameterHandler;

/**
 */
public abstract class ParameterCondition {

    protected ParameterHandler parameterHandler;

    protected String conditionParameter;

    protected boolean becomeMandatory;

    /**
     * This constructor can be used when this condition does not depend on any other parameter but
     * outer conditions as for example the version of the operator.
     */
    public ParameterCondition(ParameterHandler parameterHandler, boolean becomeMandatory) {
        this.parameterHandler = parameterHandler;
        this.conditionParameter = null;
        this.becomeMandatory = becomeMandatory;
    }

    public ParameterCondition(ParameterHandler parameterHandler, String conditionParameter, boolean becomeMandatory) {
        this.parameterHandler = parameterHandler;
        this.conditionParameter = conditionParameter;
        this.becomeMandatory = becomeMandatory;
    }

    /**
     * This returns true if the condition is met and if the ancestor type isn't hidden.
     */
    final public boolean dependencyMet() {
        // if we don't can check: Return always true
        if (parameterHandler == null) {
            return true;
        }

        // otherwise perform check
        if (conditionParameter != null) {
            if (parameterHandler.getParameters().getParameterType(conditionParameter).isHidden()) {
                return false;
            }
        }
        return isConditionFullfilled();
    }

    /**
     * Subclasses have to implement this method in order to return if the condition is fulfilled.
     */
    public abstract boolean isConditionFullfilled();

    public boolean becomeMandatory() {
        return becomeMandatory;
    }
}
