package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A parameter type for linkage categories.
 * It's values dynamic changed when observed others changed
 */
public class ParameterTypeDynamicCategory extends ParameterTypeStringCategory {

    @JsonProperty
    private String observeKey = null;

    private String[] categoryValues = new String[0];

    public ParameterTypeDynamicCategory(String key, String observeKey, String description) {
        this(key, observeKey, description, new String[0]);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description, String[] categories) {
        this(key, observeKey, description, categories, new String[0]);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description, String[] categories,
                                        String[] categoryValues) {
        this(key, observeKey, description, categories, categoryValues, null);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description, String[] categories,
                                        String[] categoryValues, String defaultValue) {
        this(key, observeKey, description, categories, categoryValues, defaultValue, true);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description, String[] categories,
                                        String[] categoryValues, String defaultValue, boolean editable) {
        super(key, description, categories, defaultValue, editable);
        this.observeKey = observeKey;
        this.categoryValues = categoryValues;
        setOptional(defaultValue != null);
    }

    public String getCategoryValue(String category) {
        String[] categories = getValues();
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(category)) {
                return categoryValues[i];
            }
        }

        return null;
    }
}
