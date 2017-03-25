package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A parameter type for linkage categories.
 * It's values dynamic changed when observed others changed
 */
public class ParameterTypeDynamicCategory extends ParameterTypeSingle {

    @JsonProperty
    private String observeKey = null;

    @JsonProperty
    private String defaultValue = null;

    @JsonProperty
    private String[] categories;

    private boolean editable = true;

    public ParameterTypeDynamicCategory(String key, String observeKey, String description) {
        this(key, observeKey, description, new String[0]);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        String[] categories) {
        this(key, observeKey, description, categories, null);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        String[] categories, String defaultValue) {
        this(key, observeKey, description, categories, defaultValue, true);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        String[] categories, String defaultValue, boolean editable) {
        super(key, description);
        this.categories = categories;
        this.defaultValue = defaultValue;
        this.editable = editable;
        this.observeKey = observeKey;
        this.categories = categories;
        setOptional(defaultValue != null);
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
    public boolean isNumerical() {
        return false;
    }

    @Override
    public String getRange() {
        return null;
    }

}
