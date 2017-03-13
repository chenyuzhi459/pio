package io.sugo.pio.ports;

/**
 */
public enum PortType {
    EXAMPLE_SET("example set", "样本集"),
    EXAMPLE_SET_INPUT("example set input", "样本集输入"),
    EXAMPLE_SET_OUTPUT("example set output", "样本集输出"),
    ORIGINAL("original", "原始样本集"),
    UNMATCHED_EXAMPLE_SET("unmatched example set", "未匹配的样本集"),
    ITEM_SETS("item sets", "项集"),
    FREQUENT_SETS("frequent sets", "频繁项集"),
    CLUSTER_SET("clustered set", "簇集"),
    TRAINING_SET("training set", "训练集"),
    RULES("rules", "规则"),
    MODEL("model", "模型"),
    CLUSTER_MODEL("cluster model", "簇模型"),
    PREPROCESSING_MODEL("preprocessing model", "预处理模型"),
    MODEL_INPUT("model input", "模型输入"),
    MODEL_OUTPUT("model output", "模型输出"),
    ORIGINAL_MODEL_OUTPUT("original model output", "原始模型输出"),
    LABELLED_DATA("labelled data", "标记的数据"),
    UNLABELLED_DATA("unlabelled data", "未标记的数据"),
    PERFORMANCE("performance", "性能"),
    ESTIMATED_PERFORMANCE("estimated performance", "性能评估"),
    THROUGH("through", "穿过"),
    START("start", "开始"),
    END("end", "结束"),
    WEIGHTS("weights", "权重"),
    OUTPUT("output", "输出"),
    FILE("file", "文件"),
    ;

    private PortType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * The port name
     */
    private String name;

    /**
     * The port description
     */
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
