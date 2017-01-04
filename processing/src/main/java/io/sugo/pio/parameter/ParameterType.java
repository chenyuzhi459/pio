package io.sugo.pio.parameter;

import io.sugo.pio.parameter.conditions.ParameterCondition;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 */
public abstract class ParameterType implements Comparable<ParameterType>, Serializable {
    /** The key of this parameter. */
    private String key;

    /** The documentation. Used as tooltip text... */
    private String description;

    /**
     * Indicates if this parameter is hidden and is not shown in the GUI. May be used in conjunction
     * with a configuration wizard which lets the user configure the parameter.
     */
    private boolean isHidden = false;

    /**
     * Indicates if this parameter is optional unless a dependency condition made it mandatory.
     */
    private boolean isOptional = true;

    /**
     * Indicates that this parameter is deprecated and remains only for compatibility reasons during
     * loading of older processes. It should neither be shown nor documented.
     */
    private boolean isDeprecated = false;


    /** Creates a new ParameterType. */
    public ParameterType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * This collection assembles all conditions to be met to show this parameter within the gui.
     */
    private final Collection<ParameterCondition> conditions = new LinkedList<>();


    /** Returns a value that can be used if the parameter is not set. */
    public abstract Object getDefaultValue();

    /** Sets the default value. */
    public abstract void setDefaultValue(Object defaultValue);

    /**
     * Returns true if this parameter is hidden or not all dependency conditions are fulfilled. Then
     * the parameter will not be shown in the GUI. The default implementation returns true which
     * should be the normal case.
     *
     * Please note that this method cannot be accessed during getParameterTypes() method
     * invocations, because it relies on getting the Parameters object, which is then not created.
     */
    public boolean isHidden() {
        boolean conditionsMet = true;
        for (ParameterCondition condition : conditions) {
            conditionsMet &= condition.dependencyMet();
        }
        return isDeprecated || isHidden || !conditionsMet;
    }

    /** Registers the given dependency condition. */
    public void registerDependencyCondition(ParameterCondition condition) {
        this.conditions.add(condition);
    }

    /**
     * This sets if the parameter is optional or must be entered. If it is not optional, it may not
     * be an expert parameter and the expert status will be ignored!
     */
    public final void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

    /**
     * Returns true if this parameter is optional. The default implementation returns true.
     */
    public final boolean isOptional() {
        if (isOptional) {
            // if parameter is optional per default: check conditions
            boolean becomeMandatory = false;
            for (ParameterCondition condition : conditions) {
                if (condition.dependencyMet()) {
                    becomeMandatory |= condition.becomeMandatory();
                } else {
                    return true;
                }
            }
            return !becomeMandatory;
        }
        // otherwise it is mandatory even without dependency
        return false;
    }

    /**
     * Returns true if the values of this parameter type are numerical, i.e. might be parsed by
     * {@link Double#parseDouble(String)}. Otherwise false should be returned. This method might be
     * used by parameter logging operators.
     */
    public abstract boolean isNumerical();

    /** Sets the key. */
    public void setKey(String key) {
        this.key = key;
    }

    /** Returns the key. */
    public String getKey() {
        return key;
    }

    /** Returns a short description. */
    public String getDescription() {
        return description;
    }

    /** Sets the short description. */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Returns a human readable description of the range. */
    public abstract String getRange();

    /** Returns a string representation of this value. */
    public String toString(Object value) {
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    @Override
    public int compareTo(ParameterType o) {
        if (!(o instanceof ParameterType)) {
            return 0;
        } else {
			/* ParameterTypes are compared by key. */
            return this.key.compareTo(o.key);
        }
    }
}
