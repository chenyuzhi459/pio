package io.sugo.pio.operator.learner;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.set.HeaderExampleSet;
import io.sugo.pio.example.set.RemappedExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.operator.AbstractModel;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.Ontology;

import java.util.Iterator;

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
     * Applies the model by creating a predicted label attribute and setting the predicted label
     * values.
     */
    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        ExampleSet mappedExampleSet = new RemappedExampleSet(exampleSet, getTrainingHeader(), false);
        checkCompatibility(mappedExampleSet);
        Attribute predictedLabel = createPredictionAttributes(mappedExampleSet, getLabel());
        ExampleSet result = performPrediction(mappedExampleSet, predictedLabel);

        // Copy in order to avoid RemappedExampleSets wrapped around each other accumulating over
        // time
        exampleSet = (ExampleSet) exampleSet.clone();
        copyPredictedLabel(result, exampleSet);

        return exampleSet;
    }

    /**
     * This method is invoked before the model is actually applied. The default implementation
     * performs some basic compatibility checks and writes warnings if the given example set (for
     * applying the model) does not fit the training example set. Subclasses might override this
     * method and might throw exceptions which will prevent the application of the model.
     */
    protected void checkCompatibility(ExampleSet exampleSet) throws OperatorException {
        ExampleSet trainingHeaderSet = getTrainingHeader();
        // check given constraints (might throw an UserError)
        ExampleSetUtilities.checkAttributesMatching(getOperator(), trainingHeaderSet.getAttributes(),
                exampleSet.getAttributes(), compareSetSize, compareDataType);
        // check number of attributes
        if (exampleSet.getAttributes().size() == trainingHeaderSet.getAttributes().size()) {
            // check order of attributes
            Iterator<Attribute> trainingIt = trainingHeaderSet.getAttributes().iterator();
            Iterator<Attribute> applyIt = exampleSet.getAttributes().iterator();
            while (trainingIt.hasNext() && applyIt.hasNext()) {
                if (!trainingIt.next().getName().equals(applyIt.next().getName())) {
                    break;
                }
            }
        }

        // check if all training attributes are part of the example set and have the same value
        // types and values
        for (Attribute trainingAttribute : trainingHeaderSet.getAttributes()) {
            String name = trainingAttribute.getName();
            Attribute attribute = exampleSet.getAttributes().getRegular(name);
            if (attribute != null) {
                if (trainingAttribute.getValueType() == attribute.getValueType()) {
                    // check nominal values
                    if (trainingAttribute.isNominal()) {
                        if (trainingAttribute.getMapping().size() == attribute.getMapping().size()) {
                            for (String v : trainingAttribute.getMapping().getValues()) {
                                int trainingIndex = trainingAttribute.getMapping().getIndex(v);
                                int applicationIndex = attribute.getMapping().getIndex(v);
                                if (trainingIndex != applicationIndex) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method creates prediction attributes like the predicted label and confidences if needed.
     */
    protected Attribute createPredictionAttributes(ExampleSet exampleSet, Attribute label) {
        // create and add prediction attribute
        Attribute predictedLabel = AttributeFactory.createAttribute(label, Attributes.PREDICTION_NAME);
        predictedLabel.clearTransformations();
        ExampleTable table = exampleSet.getExampleTable();
        table.addAttribute(predictedLabel);
        exampleSet.getAttributes().setPredictedLabel(predictedLabel);

        // check whether confidence labels should be constructed
        if (supportsConfidences(label)) {
            for (String value : predictedLabel.getMapping().getValues()) {
                Attribute confidence = AttributeFactory.createAttribute(Attributes.CONFIDENCE_NAME + "(" + value + ")",
                        Ontology.REAL);
                table.addAttribute(confidence);
                exampleSet.getAttributes().setSpecialAttribute(confidence, Attributes.CONFIDENCE_NAME + "_" + value);
            }
        }
        return predictedLabel;
    }

    /**
     * This method determines if confidence attributes are created depending on the current label.
     * Usually this depends only on the fact that the label is nominal, but subclasses might
     * override this to avoid attribute construction for confidences.
     */
    protected boolean supportsConfidences(Attribute label) {
        return label != null && label.isNominal();
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

    /**
     * Helper method in order to reduce memory consumption. This method should be invoked after a
     * predicted label and confidence are not longer needed, e.g. after each iteration of a
     * crossvalidation or after a meta learning iteration.
     */
    public static void removePredictedLabel(ExampleSet exampleSet) {
        removePredictedLabel(exampleSet, true, true);
    }

    /**
     * Helper method in order to lower memory consumption. This method should be invoked after a
     * predicted label and confidence are not longer needed, e.g. after each crossvalidation run or
     * after a meta learning iteration.
     */
    public static void removePredictedLabel(ExampleSet exampleSet, boolean removePredictionFromTable,
                                            boolean removeConfidencesFromTable) {
        Attribute predictedLabel = exampleSet.getAttributes().getPredictedLabel();
        if (predictedLabel != null) { // remove old predicted label
            if (predictedLabel.isNominal()) {
                for (String value : predictedLabel.getMapping().getValues()) {
                    Attribute currentConfidenceAttribute = exampleSet.getAttributes().getSpecial(
                            Attributes.CONFIDENCE_NAME + "_" + value);
                    if (currentConfidenceAttribute != null) {
                        exampleSet.getAttributes().remove(currentConfidenceAttribute);
                        if (removeConfidencesFromTable) {
                            exampleSet.getExampleTable().removeAttribute(currentConfidenceAttribute);
                        }
                    }
                }
            }
            exampleSet.getAttributes().remove(predictedLabel);
            if (removePredictionFromTable) {
                exampleSet.getExampleTable().removeAttribute(predictedLabel);
            }
        }
    }

    /**
     * Copies the predicted label from the source example set to the destination example set. Does
     * nothing if the source does not contain a predicted label.
     */
    public static void copyPredictedLabel(ExampleSet source, ExampleSet destination) {
        Attribute predictedLabel = source.getAttributes().getPredictedLabel();
        if (predictedLabel != null) {
            // remove attributes but do not delete the columns from the table, otherwise copying is
            // not possible
            removePredictedLabel(destination, false, false);
            if (predictedLabel.isNominal()) {
                for (String value : predictedLabel.getMapping().getValues()) {
                    Attribute currentConfidenceAttribute = source.getAttributes()
                            .getSpecial(Attributes.CONFIDENCE_NAME + "_" + value);

                    // it's possible that the model does not create confidences for all label
                    // values, so check for null (e.g. OneClass-SVM)
                    if (currentConfidenceAttribute != null) {
                        Attribute copyOfCurrentConfidenceAttribute = AttributeFactory
                                .createAttribute(currentConfidenceAttribute);
                        destination.getAttributes().setSpecialAttribute(copyOfCurrentConfidenceAttribute,
                                Attributes.CONFIDENCE_NAME + "_" + value);
                    }
                }
            }
            Attribute copyOfPredictedLabel = AttributeFactory.createAttribute(predictedLabel);
            destination.getAttributes().setPredictedLabel(copyOfPredictedLabel);
        }

        Attribute costs = source.getAttributes().getCost();
        if (costs != null) {
            destination.getAttributes().setSpecialAttribute(costs, Attributes.CLASSIFICATION_COST);
        }
    }
}
