package io.sugo.pio.operator.learner.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.set.ExampleSetUtilities.SetsCompareOption;
import io.sugo.pio.example.set.ExampleSetUtilities.TypesCompareOption;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorProgress;
import io.sugo.pio.operator.error.AttributeNotFoundError;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.tools.Tools;


/**
 * The model for linear regression.
 */
public class LinearRegressionModel extends PredictionModel {

    private static final long serialVersionUID = 8381268071090932037L;

    private static final int OPERATOR_PROGRESS_STEPS = 5000;

    @JsonProperty
    private String[] attributeNames;

    @JsonProperty
    private boolean[] selectedAttributes;

    @JsonProperty
    private double[] coefficients;

    @JsonProperty
    private double[] standardErrors;

    @JsonProperty
    private double[] tolerances;

    @JsonProperty
    private double[] standardizedCoefficients;

    @JsonProperty
    private double[] tStatistics;

    @JsonProperty
    private double[] pValues;

    @JsonProperty
    private boolean useIntercept = true;

    @JsonProperty
    private String firstClassName = null;

    @JsonProperty
    private String secondClassName = null;

    public LinearRegressionModel(ExampleSet exampleSet, boolean[] selectedAttributes, double[] coefficients,
                                 double[] standardErrors, double[] standardizedCoefficients, double[] tolerances, double[] tStatistics,
                                 double[] pValues, boolean useIntercept, String firstClassName, String secondClassName) {
        super(exampleSet, null, null);
        this.attributeNames = io.sugo.pio.example.Tools.getRegularAttributeNames(exampleSet);
        this.selectedAttributes = selectedAttributes;
        this.coefficients = coefficients;
        this.standardErrors = standardErrors;
        this.standardizedCoefficients = standardizedCoefficients;
        this.tolerances = tolerances;
        this.tStatistics = tStatistics;
        this.pValues = pValues;
        this.useIntercept = useIntercept;
        this.firstClassName = firstClassName;
        this.secondClassName = secondClassName;
    }

    @Override
    public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
        ExampleSetUtilities.checkAttributesMatching(null, this.getTrainingHeader().getAttributes(),
                exampleSet.getAttributes(), SetsCompareOption.EQUAL, TypesCompareOption.EQUAL);

        Attribute[] attributes = new Attribute[attributeNames.length];
        for (int i = 0; i < attributeNames.length; i++) {
            attributes[i] = exampleSet.getAttributes().get(attributeNames[i]);
            if (attributes[i] == null && selectedAttributes[i]) {
                throw new AttributeNotFoundError(null, null, attributeNames[i]);
            }
        }

        // initialize progress
        OperatorProgress progress = null;
        if (getShowProgress() && getOperator() != null && getOperator().getProgress() != null) {
            progress = getOperator().getProgress();
            progress.setTotal(exampleSet.size());
        }
        int progressCounter = 0;

        for (Example example : exampleSet) {
            double prediction = 0;
            int index = 0;
            int attributeCounter = 0;
            for (Attribute attribute : attributes) {
                if (selectedAttributes[attributeCounter]) {
                    prediction += coefficients[index] * example.getValue(attribute);
                    index++;
                }
                attributeCounter++;
            }

            if (useIntercept) {
                prediction += coefficients[index];
            }

            if (predictedLabel.isNominal()) {
                int predictionIndex = prediction > 0.5 ? predictedLabel.getMapping().getIndex(secondClassName)
                        : predictedLabel.getMapping().getIndex(firstClassName);
                example.setValue(predictedLabel, predictionIndex);
                // set confidence to numerical prediction, such that can be scaled later.
                // The line below calculates the logistic function of the prediction. The logistic
                // function
                // is symmetric to the point (0.0, 0.5), but we use 0.5 as a prediction threshold,
                // not 0.0.
                // For that reason we have to shift the function to the right by 0.5 by subtracting
                // that value
                // from the function argument.
                double logFunction = 1.0d / (1.0d + Math.exp(-(prediction - 0.5)));
                example.setConfidence(secondClassName, logFunction);
                example.setConfidence(firstClassName, 1 - logFunction);
            } else {
                example.setValue(predictedLabel, prediction);
            }

            if (progress != null && ++progressCounter % OPERATOR_PROGRESS_STEPS == 0) {
                progress.setCompleted(progressCounter);
            }
        }
        return exampleSet;
    }

    @JsonProperty
    public String getDescription() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        boolean first = true;
        int index = 0;
        for (int i = 0; i < selectedAttributes.length; i++) {
            if (selectedAttributes[i]) {
                result.append(getCoefficientString(coefficients[index], first) + " * " + attributeNames[i]
                        + Tools.getLineSeparator());
                index++;
                first = false;
            }
        }
        if (useIntercept) {
            result.append(getCoefficientString(coefficients[coefficients.length - 1], first));
        }
        return result.toString();
    }

    private String getCoefficientString(double coefficient, boolean first) {
        if (!first) {
            if (coefficient >= 0) {
                return "+ " + Tools.formatNumber(Math.abs(coefficient));
            } else {
                return "- " + Tools.formatNumber(Math.abs(coefficient));
            }
        } else {
            if (coefficient >= 0) {
                return "  " + Tools.formatNumber(Math.abs(coefficient));
            } else {
                return "- " + Tools.formatNumber(Math.abs(coefficient));
            }
        }
    }

    /**
     * returns an array containing all names of all attributes used for training
     */
    public String[] getAttributeNames() {
        return attributeNames;
    }

    /**
     * returns an array containing only the names of those attributes that have been selected to be
     * included into the model
     */
    public String[] getSelectedAttributeNames() {
        String[] attributeNames = new String[useIntercept ? coefficients.length - 1 : coefficients.length];
        int index = 0;
        for (int i = 0; i < selectedAttributes.length; i++) {
            if (selectedAttributes[i]) {
                attributeNames[index] = this.attributeNames[i];
                index++;
            }
        }
        return attributeNames;
    }

    public boolean[] getSelectedAttributes() {
        return selectedAttributes;
    }

    public String getFirstLabel() {
        return firstClassName;
    }

    public String getSecondLabel() {
        return secondClassName;
    }

    public boolean usesIntercept() {
        return useIntercept;
    }

    /**
     * This method will return all used coefficients. So the array will be smaller than the array of
     * used attribute names! The ordering is the same but will only contain used attributes with the
     * bias appended.
     */
    public double[] getCoefficients() {
        return coefficients;
    }

    public double[] getStandardizedCoefficients() {
        return standardizedCoefficients;
    }

    public double[] getTolerances() {
        return tolerances;
    }

    public double[] getStandardErrors() {
        return standardErrors;
    }

    public double[] getTStats() {
        return tStatistics;
    }

    public double[] getProbabilities() {
        return pValues;
    }

}