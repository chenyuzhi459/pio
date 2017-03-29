package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

public enum OperatorCategory {
    source ("source", I18N.getMessage("pio.OperatorCategory.source")),
    processing("processing", I18N.getMessage("pio.OperatorCategory.processing")),
    algorithmModel("algorithmModel", I18N.getMessage("pio.OperatorCategory.algorithmModel"));

    private final String category;
    private final String description;

    OperatorCategory(String category, String description){
        this.category = category;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}
