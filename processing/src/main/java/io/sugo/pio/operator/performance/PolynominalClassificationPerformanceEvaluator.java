package io.sugo.pio.operator.performance;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.ParameterTypeList;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.tools.Ontology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * <p>
 * This performance evaluator operator should be used for classification tasks, i.e. in cases where
 * the label attribute has a (poly-)nominal value type. The operator expects a test
 * {@link ExampleSet} as input, whose elements have both true and predicted labels, and delivers as
 * output a list of performance values according to a list of performance criteria that it
 * calculates. If an input performance vector was already given, this is used for keeping the
 * performance values.
 * </p>
 * <p>
 * <p>
 * All of the performance criteria can be switched on using boolean parameters. Their values can be
 * queried by a ProcessLogOperator using the same names. The main criterion is used for comparisons
 * and need to be specified only for processes where performance vectors are compared, e.g. feature
 * selection or other meta optimization process setups. If no other main criterion was selected, the
 * first criterion in the resulting performance vector will be assumed to be the main criterion.
 * </p>
 * <p>
 * <p>
 * The resulting performance vectors are usually compared with a standard performance comparator
 * which only compares the fitness values of the main criterion. Other implementations than this
 * simple comparator can be specified using the parameter <var>comparator_class</var>. This may for
 * instance be useful if you want to compare performance vectors according to the weighted sum of
 * the individual criteria. In order to implement your own comparator, simply subclass
 * {@link PerformanceComparator}. Please note that for true multi-objective optimization usually
 * another selection scheme is used instead of simply replacing the performance comparator.
 * </p>
 */
public class PolynominalClassificationPerformanceEvaluator extends AbstractPerformanceEvaluator {

    private static final Logger logger = new Logger(PolynominalClassificationPerformanceEvaluator.class);

    /**
     * The parameter name for &quot;The weights for all classes (first column: class name, second
     * column: weight), empty: using 1 for all classes.&quot;
     */
    public static final String PARAMETER_CLASS_WEIGHTS = "class_weights";

    /**
     * The proper criteria to the names.
     */
    private static final Class<?>[] SIMPLE_CRITERIA_CLASSES = {
            io.sugo.pio.operator.performance.AbsoluteError.class,
            io.sugo.pio.operator.performance.RelativeError.class,
            io.sugo.pio.operator.performance.LenientRelativeError.class,
            io.sugo.pio.operator.performance.StrictRelativeError.class,
            io.sugo.pio.operator.performance.NormalizedAbsoluteError.class,
            io.sugo.pio.operator.performance.RootMeanSquaredError.class,
            io.sugo.pio.operator.performance.RootRelativeSquaredError.class,
            io.sugo.pio.operator.performance.SquaredError.class,
            io.sugo.pio.operator.performance.CorrelationCriterion.class,
            io.sugo.pio.operator.performance.SquaredCorrelationCriterion.class,
            io.sugo.pio.operator.performance.CrossEntropy.class,
            io.sugo.pio.operator.performance.Margin.class,
            io.sugo.pio.operator.performance.SoftMarginLoss.class,
            io.sugo.pio.operator.performance.LogisticLoss.class
    };

    public PolynominalClassificationPerformanceEvaluator() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.PolynominalClassificationPerformanceEvaluator.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.PolynominalClassificationPerformanceEvaluator.description");
    }

    @Override
    protected void checkCompatibility(ExampleSet exampleSet) throws OperatorException {
        Tools.isLabelled(exampleSet);
        Tools.isNonEmpty(exampleSet);

        Attribute label = exampleSet.getAttributes().getLabel();
        if (!label.isNominal()) {
            throw new UserError(this, "pio.error.attribute_must_nominal", "the calculation of performance criteria for classification tasks",
                    label.getName());
        }
    }

    @Override
    protected double[] getClassWeights(Attribute label) throws UserError {
        double[] weights = null;
        if (isParameterSet(PARAMETER_CLASS_WEIGHTS)) {
            weights = new double[label.getMapping().size()];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = 1.0d;
            }
            List<String[]> classWeights = getParameterList(PARAMETER_CLASS_WEIGHTS);
            Iterator<String[]> i = classWeights.iterator();
            while (i.hasNext()) {
                String[] classWeightArray = i.next();
                String className = classWeightArray[0];
                double classWeight = Double.valueOf(classWeightArray[1]);
                if (label.getMapping().getValues().contains(className)) {
                    int index = label.getMapping().mapString(className);
                    weights[index] = classWeight;
                } else {
                    throw new UserError(this, "967", className);
                }
            }

            // logging
            List<Double> weightList = new LinkedList<Double>();
            for (double d : weights) {
                weightList.add(d);
            }
            logger.info(getName() + ": used class weights --> " + weightList);
        }
        return weights;
    }

    @Override
    public List<PerformanceCriterion> getCriteria() {
        List<PerformanceCriterion> performanceCriteria = new LinkedList<PerformanceCriterion>();

        // multi class classification criteria
        for (int i = 0; i < MultiClassificationPerformance.NAMES.length; i++) {
            performanceCriteria.add(new MultiClassificationPerformance(i));
        }

        // multi class classification criteria
        for (int i = 0; i < WeightedMultiClassPerformance.NAMES.length; i++) {
            performanceCriteria.add(new WeightedMultiClassPerformance(i));
        }

        // rank correlation criteria
        for (int i = 0; i < RankCorrelation.NAMES.length; i++) {
            performanceCriteria.add(new RankCorrelation(i));
        }

        for (int i = 0; i < SIMPLE_CRITERIA_CLASSES.length; i++) {
            try {
                performanceCriteria.add((PerformanceCriterion) SIMPLE_CRITERIA_CLASSES[i].newInstance());
            } catch (InstantiationException e) {
                // LogService.getGlobal().logError("Cannot instantiate " +
                // SIMPLE_CRITERIA_CLASSES[i] + ". Skipping...");
                logger.error("Cannot instantiate {0}. Skipping...",
                        SIMPLE_CRITERIA_CLASSES[i]);
            } catch (IllegalAccessException e) {
                // LogService.getGlobal().logError("Cannot instantiate " +
                // SIMPLE_CRITERIA_CLASSES[i] + ". Skipping...");
                logger.error("Cannot instantiate {0}. Skipping...",
                        SIMPLE_CRITERIA_CLASSES[i]);
            }
        }
        return performanceCriteria;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeList(
                PARAMETER_CLASS_WEIGHTS,
                I18N.getMessage("pio.PolynominalClassificationPerformanceEvaluator.class_weights"),
                new ParameterTypeString("class_name", I18N.getMessage("pio.PolynominalClassificationPerformanceEvaluator.class_name")),
                new ParameterTypeDouble("weight",
                        I18N.getMessage("pio.PolynominalClassificationPerformanceEvaluator.weight"),
                        0.0d, Double.POSITIVE_INFINITY, 1.0d)));
        return types;
    }

    @Override
    protected boolean canEvaluate(int valueType) {
        return Ontology.ATTRIBUTE_VALUE_TYPE.isA(valueType, Ontology.NOMINAL);
    }

    @Override
    public boolean supportsCapability(OperatorCapability capability) {
        switch (capability) {
            case BINOMINAL_LABEL:
            case POLYNOMINAL_LABEL:
                return true;
            case NUMERICAL_LABEL:
            case ONE_CLASS_LABEL:
                return false;
            case POLYNOMINAL_ATTRIBUTES:
            case BINOMINAL_ATTRIBUTES:
            case NUMERICAL_ATTRIBUTES:
            case WEIGHTED_EXAMPLES:
            case MISSING_VALUES:
                return true;
            case NO_LABEL:
            case UPDATABLE:
            case FORMULA_PROVIDER:
            default:
                return false;
        }
    }

}
