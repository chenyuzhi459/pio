package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

public enum OperatorCategory {
    source (I18N.getMessage("pio.OperatorCategory.source")),
    processing(I18N.getMessage("pio.OperatorCategory.processing")),
    algorithmModel(I18N.getMessage("pio.OperatorCategory.algorithmModel"));

    private final String description;

    OperatorCategory(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
