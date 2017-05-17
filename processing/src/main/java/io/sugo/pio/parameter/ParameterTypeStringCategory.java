package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A parameter type for categories. These are several Strings and one of these is the default value.
 * Additionally users can define other strings than these given in as pre-defined categories.
 * Operators ask for the defined String with the method
 * {@link io.sugo.pio.operator.Operator#getParameterAsString(String)}.
 */
public class ParameterTypeStringCategory extends ParameterTypeSingle {

    private static final long serialVersionUID = 1620216625117563601L;

    protected static final String ELEMENT_DEFAULT = "default";

    protected static final String ELEMENT_VALUES = "Values";

    protected static final String ELEMENT_VALUE = "Value";

    protected static final String ATTRIBUTE_IS_EDITABLE = "is-editable";
    @JsonProperty
    private String defaultValue = null;
    @JsonProperty
    private String[] categories = new String[0];
    @JsonProperty
    private String[] categoriesDesc = new String[0];
    @JsonProperty
    private boolean editable = true;


    public ParameterTypeStringCategory(String key, String description, String[] categories) {
        this(key, description, categories, null);
    }

    public ParameterTypeStringCategory(String key, String description, String[] categories, String defaultValue) {
        this(key, description, categories, defaultValue, true);
    }

    public ParameterTypeStringCategory(String key, String description, String[] categories, String defaultValue,
                                       boolean editable) {
        this(key, description, categories, categories, defaultValue, editable);
    }

    public ParameterTypeStringCategory(String key, String description, String[] categories, String[] categoriesDesc, String defaultValue,
                                       boolean editable) {
        super(key, description);
        this.categories = categories;
        this.categoriesDesc = categoriesDesc;
        this.defaultValue = defaultValue;
        this.editable = editable;
        setOptional(defaultValue != null);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (String) defaultValue;
    }

    @Override
    public String toString(Object value) {
        return (String) value;
    }

    public String[] getValues() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    /**
     * Returns false.
     */
    @Override
    public boolean isNumerical() {
        return false;
    }

    @Override
    public String getRange() {
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < categories.length; i++) {
            if (i > 0) {
                values.append(", ");
            }
            values.append(categories[i]);
        }
        values.append(defaultValue != null ? "; default: '" + defaultValue + "'" : "");
        return values.toString();
    }

}
