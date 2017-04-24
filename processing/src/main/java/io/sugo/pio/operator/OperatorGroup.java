package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

public enum OperatorGroup {
    dataSource (OperatorCategory.source, I18N.getMessage("pio.OperatorGroup.dataSource"), 0),

    fieldSetting(OperatorCategory.processing, I18N.getMessage("pio.OperatorGroup.fieldSetting"), 0),
    filtering(OperatorCategory.processing, I18N.getMessage("pio.OperatorGroup.filtering"), 1),
    sampling(OperatorCategory.processing, I18N.getMessage("pio.OperatorGroup.sampling"), 2),
    normalization(OperatorCategory.processing, I18N.getMessage("pio.OperatorGroup.normalization"), 3),
    aggregation(OperatorCategory.processing, I18N.getMessage("pio.OperatorGroup.aggregation"), 4),

    classification(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.classification"), 0),
    regression(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.regression"), 1),
    clustering(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.clustering"), 2),
    association(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.association"), 3),
    deepLearning(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.deepLearning"), 4),
    modelApply(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.modelApply"), 5),
    modelPerformance(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.modelPerformance"), 6),
    fm(OperatorCategory.algorithmModel, I18N.getMessage("pio.OperatorGroup.fm"), 7),

    script(OperatorCategory.utility, I18N.getMessage("pio.OperatorGroup.script"), 0);

    private final OperatorCategory category;
    private final String description;
    private final int sequence;

    OperatorGroup(OperatorCategory category, String description, int sequence){
        this.category = category;
        this.description = description;
        this.sequence = sequence;
    }

    public OperatorCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getSequence() {
        return sequence;
    }
}
