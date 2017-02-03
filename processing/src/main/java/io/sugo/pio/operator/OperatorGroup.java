package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

public enum OperatorGroup {
    source ("source", I18N.getMessage("pio.OperatorGroup.source")),
    processing("processing", I18N.getMessage("pio.OperatorGroup.processing")),
    algorithmModel("algorithmModel", I18N.getMessage("pio.OperatorGroup.algorithmModel"));

    private final String group;
    private final String description;

    OperatorGroup(String group, String description){
        this.group = group;
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }
}
