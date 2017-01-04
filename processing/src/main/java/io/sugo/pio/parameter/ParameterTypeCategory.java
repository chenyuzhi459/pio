package io.sugo.pio.parameter;

/**
 */
public class ParameterTypeCategory extends ParameterTypeSingle {

    private int defaultValue = 0;

    private String[] categories = new String[0];

    public ParameterTypeCategory(String key, String description, String[] categories, int defaultValue) {
        super(key, description);
        this.categories = categories;
        this.defaultValue = defaultValue;
    }

    public int getDefault() {
        return defaultValue;
    }

    @Override
    public Object getDefaultValue() {
        if (defaultValue == -1) {
            return null;
        } else {
            return categories[defaultValue];
        }
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (Integer) defaultValue;
    }

    public String getCategory(int index) {
        return categories[index];
    }

    public int getIndex(String string) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(string)) {
                return Integer.valueOf(i);
            }
        }
        // try to interpret string as number
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

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
        return values.toString() + "; default: " + categories[defaultValue];
    }
}
