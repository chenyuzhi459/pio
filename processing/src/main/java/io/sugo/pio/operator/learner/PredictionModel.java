package io.sugo.pio.operator.learner;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.set.HeaderExampleSet;
import io.sugo.pio.operator.AbstractModel;
import io.sugo.pio.operator.OperatorException;

/**
 */
public abstract class PredictionModel extends AbstractModel {
    /**
     *
     */
    private static final long serialVersionUID = 6295359038239089617L;

    /**
     * This parameter specifies the data types at which the model can be applied on.
     */
    private ExampleSetUtilities.TypesCompareOption compareDataType;

    /**
     * This parameter specifies the relation between the training {@link ExampleSet} and the input
     * {@link ExampleSet} which is needed to apply the model on the input {@link ExampleSet}.
     */
    private ExampleSetUtilities.SetsCompareOption compareSetSize;

    /**
     * Creates a new prediction model which is build based on the given {@link ExampleSet}. Please
     * note that the given ExampleSet is automatically transformed into a {@link HeaderExampleSet}
     * which means that no reference to the data itself is kept but only to the header, i.e., to the
     * attribute meta descriptions.
     *
     * @param sizeCompareOperator
     *            describes the allowed relations between the given ExampleSet and future
     *            ExampleSets on which this Model will be applied. If this parameter is null no
     *            error will be thrown.
     * @param typeCompareOperator
     *            describes the allowed relations between the types of the attributes of the given
     *            ExampleSet and the types of future attributes of ExampleSet on which this Model
     *            will be applied. If this parameter is null no error will be thrown.
     */
    protected PredictionModel(ExampleSet trainingExampleSet, ExampleSetUtilities.SetsCompareOption sizeCompareOperator,
                              ExampleSetUtilities.TypesCompareOption typeCompareOperator) {
        super(trainingExampleSet);
        this.compareDataType = typeCompareOperator;
        this.compareSetSize = sizeCompareOperator;
    }

    /**
     * Subclasses should iterate through the given example set and set the prediction for each
     * example. The given predicted label attribute was already be added to the example set and
     * should be used to set the predicted values.
     */
    public abstract ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException;

    /** Returns the label attribute. */
    public Attribute getLabel() {
        return getTrainingHeader().getAttributes().getLabel();
    }
}
