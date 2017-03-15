package io.sugo.pio.operator.performance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.tools.Tools;
import io.sugo.pio.tools.math.Averagable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Measures the weighted mean of all per class recalls or per class precisions based on the weights
 * defined in the performance evaluator.
 */
public class WeightedMultiClassPerformance extends MeasuredPerformance implements ClassWeightedPerformance {

    private static final long serialVersionUID = 8734250559680229116L;

    /**
     * Indicates an undefined type (should not happen).
     */
    public static final int UNDEFINED = -1;

    /**
     * Indicates accuracy.
     */
    public static final int WEIGHTED_RECALL = 0;

    /**
     * Indicates classification error.
     */
    public static final int WEIGHTED_PRECISION = 1;

    /**
     * The names of the criteria.
     */
    public static final String[] NAMES = {
            I18N.getMessage("pio.WeightedMultiClassPerformance.name1"),
            I18N.getMessage("pio.WeightedMultiClassPerformance.name2")
    };

    /**
     * The descriptions of the criteria.
     */
    public static final String[] DESCRIPTIONS = {
            I18N.getMessage("pio.WeightedMultiClassPerformance.description1"),
            I18N.getMessage("pio.WeightedMultiClassPerformance.description2")
    };

    /**
     * The counter for true labels and the prediction.
     */
    @JsonProperty
    private double[][] counter;

    /**
     * The class names of the label. Used for logging and result display.
     */
    @JsonProperty
    private String[] classNames;

    /**
     * Maps class names to indices.
     */
    @JsonProperty
    private Map<String, Integer> classNameMap = new HashMap<String, Integer>();

    /**
     * The type of this performance.
     */
    private int type = WEIGHTED_RECALL;

    /**
     * The different class weights.
     */
    @JsonProperty
    private double[] classWeights;

    /**
     * The sum of all weights.
     */
    @JsonProperty
    private double weightSum;

    /**
     * The currently used label attribute.
     */
    private Attribute labelAttribute;

    /**
     * The currently used predicted label attribute.
     */
    private Attribute predictedLabelAttribute;

    /**
     * The weight attribute. Might be null.
     */
    private Attribute weightAttribute;

    /**
     * Creates a WeightedMultiClassPerformance with undefined type.
     */
    public WeightedMultiClassPerformance() {
        this(UNDEFINED);
    }

    /**
     * Creates a WeightedMultiClassPerformance with the given type.
     */
    public WeightedMultiClassPerformance(int type) {
        this.type = type;
    }

    public WeightedMultiClassPerformance(WeightedMultiClassPerformance m) {
        super(m);
        this.type = m.type;
        this.classNames = new String[m.classNames.length];
        for (int i = 0; i < this.classNames.length; i++) {
            this.classNames[i] = m.classNames[i];
            this.classNameMap.put(this.classNames[i], i);
        }
        this.counter = new double[m.counter.length][m.counter.length];
        for (int i = 0; i < this.counter.length; i++) {
            for (int j = 0; j < this.counter[i].length; j++) {
                this.counter[i][j] = m.counter[i][j];
            }
        }
        this.labelAttribute = (Attribute) m.labelAttribute.clone();
        this.predictedLabelAttribute = (Attribute) m.predictedLabelAttribute.clone();
        if (m.weightAttribute != null) {
            this.weightAttribute = (Attribute) m.weightAttribute.clone();
        }
    }

    /**
     * Creates a WeightedMultiClassPerformance with the given type.
     */
    public static WeightedMultiClassPerformance newInstance(String name) {
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(name)) {
                return new WeightedMultiClassPerformance(i);
            }
        }
        return null;
    }

    /**
     * Sets the class weights.
     */
    @Override
    public void setWeights(double[] weights) {
        this.weightSum = 0.0d;
        this.classWeights = weights;
        for (double w : this.classWeights) {
            this.weightSum += w;
        }
    }

    /**
     * Initializes the criterion and sets the label.
     */
    @Override
    public void startCounting(ExampleSet eSet, boolean useExampleWeights) throws OperatorException {
        super.startCounting(eSet, useExampleWeights);
        this.labelAttribute = eSet.getAttributes().getLabel();
        if (!this.labelAttribute.isNominal()) {
            throw new UserError(null, "pio.error.attribute_must_nominal",
                    "calculation of classification performance criteria",
                    this.labelAttribute.getName());
        }
        this.predictedLabelAttribute = eSet.getAttributes().getPredictedLabel();
        if (this.predictedLabelAttribute == null || !this.predictedLabelAttribute.isNominal()) {
            throw new UserError(null, "pio.error.attribute_must_nominal",
                    "calculation of classification performance criteria", "predicted label attribute");
        }

        if (useExampleWeights) {
            this.weightAttribute = eSet.getAttributes().getWeight();
        }

        List<String> values = labelAttribute.getMapping().getValues();
        this.counter = new double[values.size()][values.size()];
        this.classNames = new String[values.size()];
        Iterator<String> i = values.iterator();
        int n = 0;
        while (i.hasNext()) {
            classNames[n] = i.next();
            classNameMap.put(classNames[n], n);
            n++;
        }
    }

    /**
     * Increases the prediction value in the matrix.
     */
    @Override
    public void countExample(Example example) {
        int label = classNameMap.get(example.getNominalValue(labelAttribute));
        int plabel = classNameMap.get(example.getNominalValue(predictedLabelAttribute));
        double weight = 1.0d;
        if (weightAttribute != null) {
            weight = example.getValue(weightAttribute);
        }
        counter[label][plabel] += weight;
    }

    @Override
    public double getExampleCount() {
        double total = 0;
        for (int i = 0; i < counter.length; i++) {
            for (int j = 0; j < counter[i].length; j++) {
                total += counter[i][j];
            }
        }
        return total;
    }

    /**
     * Returns either the accuracy or the classification error.
     */
    @Override
    public double getMikroAverage() {
        switch (type) {
            case WEIGHTED_RECALL:
                double[] columnSums = new double[classNames.length];
                for (int c = 0; c < columnSums.length; c++) {
                    for (int r = 0; r < counter[c].length; r++) {
                        columnSums[c] += counter[c][r];
                    }
                }
                double result = 0.0d;
                for (int c = 0; c < columnSums.length; c++) {
                    double r = counter[c][c] / columnSums[c];
                    if (classWeights != null) {
                        result += classWeights[c] * (Double.isNaN(r) ? 0 : r);
                    }
                }
                result /= weightSum;
                return result;
            case WEIGHTED_PRECISION:
                double[] rowSums = new double[classNames.length];
                for (int r = 0; r < counter.length; r++) {
                    for (int c = 0; c < counter[r].length; c++) {
                        rowSums[r] += counter[c][r];
                    }
                }
                result = 0.0d;
                for (int r = 0; r < rowSums.length; r++) {
                    double p = counter[r][r] / rowSums[r];
                    if (classWeights != null) {
                        result += classWeights[r] * (Double.isNaN(p) ? 0 : p);
                    }
                }
                result /= weightSum;
                return result;
            default:
                throw new RuntimeException("Unknown type " + type + " for weighted multi class performance criterion!");
        }
    }

    /**
     * Returns true.
     */
    @Override
    public boolean formatPercent() {
        return true;
    }

    @Override
    public double getMikroVariance() {
        return Double.NaN;
    }

    /**
     * Returns the name.
     */
    @Override
    @JsonProperty
    public String getName() {
        return NAMES[type];
    }

    /**
     * Returns the description.
     */
    @Override
    public String getDescription() {
        return DESCRIPTIONS[type];
    }

    // ================================================================================

    /**
     * Returns the accuracy or 1 - error.
     */
    @Override
    public double getFitness() {
        return getAverage();
    }

    /**
     * Returns 1.
     */
    @Override
    public double getMaxFitness() {
        return 1.0d;
    }

    @Override
    public void buildSingleAverage(Averagable performance) {
        WeightedMultiClassPerformance other = (WeightedMultiClassPerformance) performance;
        for (int i = 0; i < this.counter.length; i++) {
            for (int j = 0; j < this.counter[i].length; j++) {
                this.counter[i][j] += other.counter[i][j];
            }
        }
    }

    public String toWeightString() {
        StringBuffer result = new StringBuffer(super.toString());
        result.append(", weights: ");
        boolean first = true;
        for (double w : this.classWeights) {
            if (!first) {
                result.append(", ");
            }
            result.append(Tools.formatIntegerIfPossible(w));
            first = false;
        }
        return result.toString();
    }

    // ================================================================================

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(toWeightString() + "");
        result.append(Tools.getLineSeparator() + "ConfusionMatrix:" + Tools.getLineSeparator() + "True:");
        for (int i = 0; i < this.counter.length; i++) {
            result.append("\t" + classNames[i]);
        }

        for (int i = 0; i < this.counter.length; i++) {
            result.append(Tools.getLineSeparator() + classNames[i] + ":");
            for (int j = 0; j < this.counter[i].length; j++) {
                result.append("\t" + Tools.formatIntegerIfPossible(this.counter[j][i]));
            }
        }
        return result.toString();
    }

    public String[] getClassNames() {
        return classNames;
    }

    public double[][] getCounter() {
        return counter;
    }
}
