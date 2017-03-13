package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.parameter.conditions.ParameterCondition;
import io.sugo.pio.parameter.extension.jdbc.ParameterTypeDatabaseConnection;
import io.sugo.pio.parameter.extension.jdbc.ParameterTypeDatabaseSchema;
import io.sugo.pio.parameter.extension.jdbc.ParameterTypeDatabaseTable;
import io.sugo.pio.parameter.extension.jdbc.ParameterTypeSQLQuery;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paramType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "param_type_int", value = ParameterTypeInt.class),
        @JsonSubTypes.Type(name = "param_type_string", value = ParameterTypeString.class),
        @JsonSubTypes.Type(name = "param_type_text", value = ParameterTypeText.class),
        @JsonSubTypes.Type(name = "param_type_boolean", value = ParameterTypeBoolean.class),
        @JsonSubTypes.Type(name = "param_type_double", value = ParameterTypeDouble.class),
        @JsonSubTypes.Type(name = "param_type_long", value = ParameterTypeLong.class),
        @JsonSubTypes.Type(name = "param_type_date", value = ParameterTypeDate.class),
        @JsonSubTypes.Type(name = "param_type_date_format", value = ParameterTypeDateFormat.class),
        @JsonSubTypes.Type(name = "param_type_attribute", value = ParameterTypeAttribute.class),
        @JsonSubTypes.Type(name = "param_type_attributes", value = ParameterTypeAttributes.class),
        @JsonSubTypes.Type(name = "param_type_category", value = ParameterTypeCategory.class),
        @JsonSubTypes.Type(name = "param_type_string_category", value = ParameterTypeStringCategory.class),
        @JsonSubTypes.Type(name = "param_type_dynamic_category", value = ParameterTypeDynamicCategory.class),
        @JsonSubTypes.Type(name = "param_type_configuration", value = ParameterTypeConfiguration.class),
        @JsonSubTypes.Type(name = "param_type_enum", value = ParameterTypeEnumeration.class),
        @JsonSubTypes.Type(name = "param_type_filter", value = ParameterTypeFilter.class),
        @JsonSubTypes.Type(name = "param_type_list", value = ParameterTypeList.class),
        @JsonSubTypes.Type(name = "param_type_password", value = ParameterTypePassword.class),
        @JsonSubTypes.Type(name = "param_type_regexp", value = ParameterTypeRegexp.class),
        @JsonSubTypes.Type(name = "param_type_tuple", value = ParameterTypeTuple.class),
        @JsonSubTypes.Type(name = "param_type_repository", value = ParameterTypeRepositoryLocation.class),
        @JsonSubTypes.Type(name = "param_type_file", value = ParameterTypeFile.class),

        @JsonSubTypes.Type(name = "param_type_db_connection", value = ParameterTypeDatabaseConnection.class),
        @JsonSubTypes.Type(name = "param_type_db_schema", value = ParameterTypeDatabaseSchema.class),
        @JsonSubTypes.Type(name = "param_type_db_table", value = ParameterTypeDatabaseTable.class),
        @JsonSubTypes.Type(name = "param_type_sql_query", value = ParameterTypeSQLQuery.class)

})
public abstract class ParameterType implements Comparable<ParameterType>, Serializable {
    /**
     * The key of this parameter.
     */
    @JsonProperty
    private String key;

    /**
     * The full name of this parameter.
     */
    @JsonProperty
    private String fullName;

    /**
     * The documentation. Used as tooltip text...
     */
    @JsonProperty
    private String description;

    /**
     * Indicates if this is a parameter only viewable in expert mode. Mandatory parameters are
     * always viewable. The default value is true.
     */
    private boolean expert = true;

    /**
     * Indicates if this parameter is hidden and is not shown in the GUI. May be used in conjunction
     * with a configuration wizard which lets the user configure the parameter.
     */
    @JsonProperty
    private boolean isHidden = false;

    /**
     * Indicates if this parameter is optional unless a dependency condition made it mandatory.
     */
    @JsonProperty
    private boolean isOptional = true;

    /**
     * Indicates that this parameter is deprecated and remains only for compatibility reasons during
     * loading of older processes. It should neither be shown nor documented.
     */
    private boolean isDeprecated = false;

    /**
     * Creates a new ParameterType.
     */
    public ParameterType(String key, String fullName) {
        this.key = key;
        this.fullName = fullName;
        this.description = fullName;
    }

    /**
     * This collection assembles all conditions to be met to show this parameter within the gui.
     */
    @JsonProperty
    private final Collection<ParameterCondition> conditions = new LinkedList<>();


    /**
     * Returns a value that can be used if the parameter is not set.
     */
    public abstract Object getDefaultValue();

    /**
     * Sets the default value.
     */
    public abstract void setDefaultValue(Object defaultValue);

    /**
     * Returns true if this parameter can only be seen in expert mode. The default implementation
     * returns true if the parameter is optional. It is ensured that an non-optional parameter is
     * never expert!
     */
    public boolean isExpert() {
        return expert && isOptional;
    }

    /**
     * Sets if this parameter can be seen in expert mode (true) or beginner mode (false).
     *
     */
    public void setExpert(boolean expert) {
        this.expert = expert;
    }

    /**
     * Returns true if this parameter is hidden or not all dependency conditions are fulfilled. Then
     * the parameter will not be shown in the GUI. The default implementation returns true which
     * should be the normal case.
     * <p>
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

    public Collection<ParameterCondition> getConditions() {
        return Collections.unmodifiableCollection(conditions);
    }

    /**
     * Sets if this parameter is hidden (value true) and will not be shown in the GUI.
     */
    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    /**
     * Registers the given dependency condition.
     */
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
     * This returns whether this parameter is deprecated.
     */
    public boolean isDeprecated() {
        return this.isDeprecated;
    }

    /**
     * This method indicates that this parameter is deprecated and isn't used anymore beside from
     * loading old process files.
     */
    public void setDeprecated() {
        this.isDeprecated = true;
    }

    /**
     * Returns true if the values of this parameter type are numerical, i.e. might be parsed by
     * {@link Double#parseDouble(String)}. Otherwise false should be returned. This method might be
     * used by parameter logging operators.
     */
    public abstract boolean isNumerical();

    /**
     * Sets the key.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns a short description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the short description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns a human readable description of the range.
     */
    public abstract String getRange();

    /**
     * Returns a string representation of this value.
     */
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
