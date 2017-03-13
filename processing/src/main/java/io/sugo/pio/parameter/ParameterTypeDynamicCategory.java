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
    private DynamicCategoryCombo[] categories;

    private boolean editable = true;

    public ParameterTypeDynamicCategory(String key, String observeKey, String description) {
        this(key, observeKey, description, new DynamicCategoryCombo[0]);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        DynamicCategoryCombo[] categories) {
        this(key, observeKey, description, categories, null);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        DynamicCategoryCombo[] categories, String defaultValue) {
        this(key, observeKey, description, categories, defaultValue, true);
    }

    public ParameterTypeDynamicCategory(String key, String observeKey, String description,
                                        DynamicCategoryCombo[] categories, String defaultValue, boolean editable) {
        super(key, description);
        this.categories = categories;
        this.defaultValue = defaultValue;
        this.editable = editable;
        this.observeKey = observeKey;
        this.categories = categories;
        setOptional(defaultValue != null);
    }

    public String getCategoryName(String categoryId) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].getId().equals(categoryId)) {
                return categories[i].getName();
            }
        }

        return null;
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

    public static final class DynamicCategoryCombo {
        @JsonProperty
        String id;
        @JsonProperty
        String name;
        @JsonProperty
        String desc;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
