package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

public enum OperatorGroup {
    dataSource (OperatorCategory.source, "dataSource", I18N.getMessage("pio.OperatorGroup.dataSource"), 0),

    fieldSetting(OperatorCategory.processing, "fieldSetting", I18N.getMessage("pio.OperatorGroup.fieldSetting"), 0),
    filtering(OperatorCategory.processing, "filtering", I18N.getMessage("pio.OperatorGroup.filtering"), 1),
    sampling(OperatorCategory.processing, "sampling", I18N.getMessage("pio.OperatorGroup.sampling"), 2),
    normalization(OperatorCategory.processing, "normalization", I18N.getMessage("pio.OperatorGroup.normalization"), 3),
    aggregation(OperatorCategory.processing, "aggregation", I18N.getMessage("pio.OperatorGroup.aggregation"), 4),

    classification(OperatorCategory.algorithmModel, "classification", I18N.getMessage("pio.OperatorGroup.classification"), 0),
    regression(OperatorCategory.algorithmModel, "regression", I18N.getMessage("pio.OperatorGroup.regression"), 1),
    clustering(OperatorCategory.algorithmModel, "clustering", I18N.getMessage("pio.OperatorGroup.clustering"), 2),
    association(OperatorCategory.algorithmModel, "association", I18N.getMessage("pio.OperatorGroup.association"), 3),
    deepLearning(OperatorCategory.algorithmModel, "deepLearning", I18N.getMessage("pio.OperatorGroup.deepLearning"), 4),
    modelApply(OperatorCategory.algorithmModel, "modelApply", I18N.getMessage("pio.OperatorGroup.modelApply"), 5),
    modelPerformance(OperatorCategory.algorithmModel, "modelPerformance", I18N.getMessage("pio.OperatorGroup.modelPerformance"), 6);

    private final OperatorCategory category;
    private final String group;
    private final String description;
    private final int sequence;

    OperatorGroup(OperatorCategory category, String group, String description, int sequence){
        this.category = category;
        this.group = group;
        this.description = description;
        this.sequence = sequence;
    }

    public OperatorCategory getCategory() {
        return category;
    }

    public String getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }

    public int getSequence() {
        return sequence;
    }
}
