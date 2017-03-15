package io.sugo.pio.ports;

import io.sugo.pio.i18n.I18N;

/**
 */
public enum PortType {
    EXAMPLES("examples", I18N.getMessage("pio.PortType.examples")),
    EXAMPLE_SET("example set", I18N.getMessage("pio.PortType.example_set")),
    EXAMPLE_SET_INPUT("example set input", I18N.getMessage("pio.PortType.example_set_input")),
    EXAMPLE_SET_OUTPUT("example set output", I18N.getMessage("pio.PortType.example_set_output")),
    ORIGINAL("original", I18N.getMessage("pio.PortType.original")),
    TRAINING_EXAMPLES("training examples", I18N.getMessage("pio.PortType.training_examples")),
    UNMATCHED_EXAMPLE_SET("unmatched example set", I18N.getMessage("pio.PortType.unmatched_example_set")),
    ITEM_SETS("item sets", I18N.getMessage("pio.PortType.item_sets")),
    FREQUENT_SETS("frequent sets", I18N.getMessage("pio.PortType.frequent_sets")),
    CLUSTER_SET("clustered set", I18N.getMessage("pio.PortType.cluster_set")),
    TRAINING_SET("training set", I18N.getMessage("pio.PortType.training_set")),
    RULES("rules", I18N.getMessage("pio.PortType.rules")),
    MODEL("model", I18N.getMessage("pio.PortType.model")),
    CLUSTER_MODEL("cluster model", I18N.getMessage("pio.PortType.cluster_model")),
    PREPROCESSING_MODEL("preprocessing model", I18N.getMessage("pio.PortType.preprocessing_model")),
    MODEL_INPUT("model input", I18N.getMessage("pio.PortType.model_input")),
    MODEL_OUTPUT("model output", I18N.getMessage("pio.PortType.model_output")),
    ORIGINAL_MODEL_OUTPUT("original model output", I18N.getMessage("pio.PortType.original_model_output")),
    LABELLED_DATA("labelled data", I18N.getMessage("pio.PortType.labelled_data")),
    UNLABELLED_DATA("unlabelled data", I18N.getMessage("pio.PortType.unlabelled_data")),
    PERFORMANCE("performance", I18N.getMessage("pio.PortType.performance")),
    ESTIMATED_PERFORMANCE("estimated performance", I18N.getMessage("pio.PortType.estimated_performance")),
    THROUGH("through", I18N.getMessage("pio.PortType.through")),
    START("start", I18N.getMessage("pio.PortType.start")),
    END("end", I18N.getMessage("pio.PortType.end")),
    WEIGHTS("weights", I18N.getMessage("pio.PortType.weights")),
    OUTPUT("output", I18N.getMessage("pio.PortType.output")),
    FILE("file", I18N.getMessage("pio.PortType.file")),
    VECTOR("vector", I18N.getMessage("pio.PortType.vector"))
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
