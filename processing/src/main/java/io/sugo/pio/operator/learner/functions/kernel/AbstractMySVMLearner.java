package io.sugo.pio.operator.learner.functions.kernel;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.*;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.*;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.operator.performance.EstimatedPerformance;
import io.sugo.pio.operator.performance.PerformanceVector;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.MetaDataError;
import io.sugo.pio.ports.metadata.SimpleMetaDataError;

import java.util.List;


/**
 * This is the abstract superclass for the support vector machine / KLR implementations of Stefan
 * R&uuml;ping.
 *
 * @rapidminer.reference Rueping/2000a
 * @rapidminer.reference Vapnik/98a
 * @rapidminer.index SVM
 */
public abstract class AbstractMySVMLearner extends AbstractKernelBasedLearner {

    private static final Logger logger = new Logger(AbstractMySVMLearner.class);

    /**
     * The parameter name for &quot;The SVM kernel parameter gamma (radial, anova).&quot;
     */
    public static final String PARAMETER_KERNEL_GAMMA = "kernel_gamma";

    /**
     * The parameter name for &quot;The SVM kernel parameter sigma1 (epanechnikov, gaussian
     * combination, multiquadric).&quot;
     */
    public static final String PARAMETER_KERNEL_SIGMA1 = "kernel_sigma1";

    /**
     * The parameter name for &quot;The SVM kernel parameter sigma2 (gaussian combination).&quot;
     */
    public static final String PARAMETER_KERNEL_SIGMA2 = "kernel_sigma2";

    /**
     * The parameter name for &quot;The SVM kernel parameter sigma3 (gaussian combination).&quot;
     */
    public static final String PARAMETER_KERNEL_SIGMA3 = "kernel_sigma3";

    /**
     * The parameter name for &quot;The SVM kernel parameter shift (multiquadric).&quot;
     */
    public static final String PARAMETER_KERNEL_SHIFT = "kernel_shift";

    /**
     * The parameter name for &quot;The SVM kernel parameter degree (polynomial, anova,
     * epanechnikov).&quot;
     */
    public static final String PARAMETER_KERNEL_DEGREE = "kernel_degree";

    /**
     * The parameter name for &quot;The SVM kernel parameter a (neural).&quot;
     */
    public static final String PARAMETER_KERNEL_A = "kernel_a";

    /**
     * The parameter name for &quot;The SVM kernel parameter b (neural).&quot;
     */
    public static final String PARAMETER_KERNEL_B = "kernel_b";

    /**
     * The parameter name for &quot;Size of the cache for kernel evaluations im MB &quot;
     */
    public static final String PARAMETER_KERNEL_CACHE = "kernel_cache";

    /**
     * The parameter name for &quot;Precision on the KKT conditions&quot;
     */
    public static final String PARAMETER_CONVERGENCE_EPSILON = "convergence_epsilon";

    /**
     * The parameter name for &quot;Stop after this many iterations&quot;
     */
    public static final String PARAMETER_MAX_ITERATIONS = "max_iterations";

    /**
     * The parameter name for &quot;Scale the example values and store the scaling parameters for
     * test set.&quot;
     */
    public static final String PARAMETER_SCALE = "scale";

    /**
     * The parameter name for &quot;Indicates if final optimization fitness should be returned as
     * performance.&quot;
     */
    public static final String PARAMETER_RETURN_OPTIMIZATION_PERFORMANCE = "return_optimization_performance";

    public static final String PARAMETER_C = "C";

    public static final String PARAMETER_KERNEL_TYPE = "kernel_type";

    public static final String PARAMETER_CALCULATE_WEIGHTS = "calculate_weights";

    /**
     * The kernels which can be used from RapidMiner for the mySVM / myKLR.
     */
    /*public static final String[] KERNEL_TYPES = {"dot", "radial", "polynomial", "neural", "anova", "epachnenikov",
            "gaussian_combination", "multiquadric"};*/
    public static final String[] KERNEL_TYPES = {
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.dot"),
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.radial"),
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.polynomial"),
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.neural"),
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.anova"),
//            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.epachnenikov"),
            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.gaussian_combination")
//            I18N.getMessage("pio.AbstractMySVMLearner.kernel_type.multiquadric")
    };

    /**
     * Indicates a linear kernel.
     */
    public static final int KERNEL_DOT = 0;

    /**
     * Indicates a rbf kernel.
     */
    public static final int KERNEL_RADIAL = 1;

    /**
     * Indicates a polynomial kernel.
     */
    public static final int KERNEL_POLYNOMIAL = 2;

    /**
     * Indicates a neural net kernel.
     */
    public static final int KERNEL_NEURAL = 3;

    /**
     * Indicates an anova kernel.
     */
    public static final int KERNEL_ANOVA = 4;

    /**
     * Indicates a epanechnikov kernel.
     */
//    public static final int KERNEL_EPANECHNIKOV = 5;

    /**
     * Indicates a gaussian combination kernel.
     */
//    public static final int KERNEL_GAUSSIAN_COMBINATION = 6;
    public static final int KERNEL_GAUSSIAN_COMBINATION = 5;

    /**
     * Indicates a multiquadric kernel.
     */
//    public static final int KERNEL_MULTIQUADRIC = 7;

    /**
     * The SVM which is used for learning.
     */
    private SVMInterface svm = null;

    /**
     * The SVM kernel.
     */
    private Kernel kernel;

    /**
     * The SVM example set.
     */
    private io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples svmExamples;

    public AbstractMySVMLearner() {
        super();
    }

    @Override
    public MetaDataError getWeightCalculationError(OutputPort weightPort) {
        try {
            /*return new SimpleMetaDataError(Severity.ERROR, weightPort,
                    Collections.singletonList(new ParameterSettingQuickFix(this, PARAMETER_KERNEL_TYPE, "0",
							"correct_parameter_settings_by", PARAMETER_KERNEL_TYPE, KERNEL_TYPES[0])),
					"parameters.setting_incompatible_for_delivering", "AttributeWeights", PARAMETER_KERNEL_TYPE,
					KERNEL_TYPES[getParameterAsInt(PARAMETER_KERNEL_TYPE)]);*/
            return new SimpleMetaDataError(Severity.ERROR, weightPort, "",
                    "parameters.setting_incompatible_for_delivering", "AttributeWeights", PARAMETER_KERNEL_TYPE,
                    KERNEL_TYPES[getParameterAsInt(PARAMETER_KERNEL_TYPE)]);
        } catch (UndefinedParameterError e) {
            return super.getWeightCalculationError(weightPort);
        }
    }

    /**
     * Creates a new SVM according to the given label.
     */
    public abstract SVMInterface createSVM(Attribute label, Kernel kernel,
                                           io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples svmExamples,
                                           ExampleSet rapidMinerExamples) throws OperatorException;

    /**
     * Creates a new SVM model from the given data.
     */
    public abstract AbstractMySVMModel createSVMModel(ExampleSet exampleSet,
                                                      io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples svmExamples, Kernel kernel,
                                                      int kernelType);

    /**
     * Returns the kernel of this SVM.
     */
    protected Kernel getKernel() {
        return kernel;
    }

    /**
     * Returns the used SVM.
     */
    protected SVMInterface getSVM() {
        return svm;
    }

    /**
     * Returns the value of the corresponding parameter.
     */
    @Override
    public boolean shouldDeliverOptimizationPerformance() {
        return getParameterAsBoolean(PARAMETER_RETURN_OPTIMIZATION_PERFORMANCE);
    }

    /**
     * Returns the optimization performance of the best result. This method must be called after
     * training, not before.
     */
    @Override
    public PerformanceVector getOptimizationPerformance() {
        double finalFitness = getFitness(svmExamples.get_alphas(), svmExamples.get_ys(), kernel);
        PerformanceVector result = new PerformanceVector();
        result.addCriterion(new EstimatedPerformance("svm_objective_function", finalFitness, 1, false));
        result.addCriterion(new EstimatedPerformance("no_support_vectors", svmExamples.getNumberOfSupportVectors(), 1, true));
        return result;
    }

    /**
     * Returns true if the user has specified that weights should be calculated.
     */
    @Override
    public boolean shouldCalculateWeights() {
        return getParameterAsBoolean(PARAMETER_CALCULATE_WEIGHTS);
    }

    @Override
    public boolean canCalculateWeights() {
        try {
            return getParameterAsInt(PARAMETER_KERNEL_TYPE) == KERNEL_DOT;
        } /*catch (UndefinedParameterError e) {
            return false;
        } */catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Returns the weights for all features.
     */
    @Override
    public AttributeWeights getWeights(ExampleSet exampleSet) throws OperatorException {
        if (getParameterAsInt(PARAMETER_KERNEL_TYPE) != KERNEL_DOT) {
            throw new UserError(this, "pio.error.operator.learner_cannot_weights",
                    this, "Cannot create weights for nonlinear kernel!");
        }
        double[] weights = svm.getWeights();
        AttributeWeights weightVector = new AttributeWeights();
        int i = 0;
        for (Attribute attribute : exampleSet.getAttributes()) {
            weightVector.setWeight(attribute.getName(), weights[i++]);
        }
        return weightVector;
    }

    @Override
    public Model learn(ExampleSet exampleSet) throws OperatorException {
        logger.info("AbstractMySVMLearner begin to learn, example set size[%d].", exampleSet.size());

        Attribute label = exampleSet.getAttributes().getLabel();
        if (label.isNominal() && label.getMapping().size() != 2) {
            throw new UserError(this, "pio.error.attribute_must_binominal", getName(), label.getName());
        }

        // check if example set contains missing values, if so fail because
        // this operator produces garbage with them
//		Tools.onlyNonMissingValues(exampleSet, getOperatorClassName(), this, Attributes.LABEL_NAME);
        Tools.onlyNonMissingValues(exampleSet, this.getClass().getName(), this, Attributes.LABEL_NAME);

        this.svmExamples = new io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples(exampleSet,
                label, getParameterAsBoolean(PARAMETER_SCALE));

        // kernel
        int cacheSize = getParameterAsInt(PARAMETER_KERNEL_CACHE);
        int kernelType = getParameterAsInt(PARAMETER_KERNEL_TYPE);
        kernel = createKernel(kernelType);
        if (kernelType == KERNEL_RADIAL) {
            ((KernelRadial) kernel).setGamma(getParameterAsDouble(PARAMETER_KERNEL_GAMMA));
        } else if (kernelType == KERNEL_POLYNOMIAL) {
            ((KernelPolynomial) kernel).setDegree(getParameterAsDouble(PARAMETER_KERNEL_DEGREE));
        } else if (kernelType == KERNEL_NEURAL) {
            ((KernelNeural) kernel).setParameters(getParameterAsDouble(PARAMETER_KERNEL_A),
                    getParameterAsDouble(PARAMETER_KERNEL_B));
        } else if (kernelType == KERNEL_ANOVA) {
            ((KernelAnova) kernel).setParameters(getParameterAsDouble(PARAMETER_KERNEL_GAMMA),
                    getParameterAsDouble(PARAMETER_KERNEL_DEGREE));
        } /*else if (kernelType == KERNEL_EPANECHNIKOV) {
            ((KernelEpanechnikov) kernel).setParameters(getParameterAsDouble(PARAMETER_KERNEL_SIGMA1),
                    getParameterAsDouble(PARAMETER_KERNEL_DEGREE));
        }*/ else if (kernelType == KERNEL_GAUSSIAN_COMBINATION) {
            ((KernelGaussianCombination) kernel).setParameters(getParameterAsDouble(PARAMETER_KERNEL_SIGMA1),
                    getParameterAsDouble(PARAMETER_KERNEL_SIGMA2), getParameterAsDouble(PARAMETER_KERNEL_SIGMA3));
        } /*else if (kernelType == KERNEL_MULTIQUADRIC) {
            ((KernelMultiquadric) kernel).setParameters(getParameterAsDouble(PARAMETER_KERNEL_SIGMA1),
                    getParameterAsDouble(PARAMETER_KERNEL_SHIFT));
        }*/
        kernel.init(svmExamples, cacheSize);
        collectLog("Create and init kernel successfully!");

        // SVM
        svm = createSVM(label, kernel, svmExamples, exampleSet);
        svm.init(kernel, svmExamples);
        svm.train();
        collectLog("SVM training finished.");

        return createSVMModel(exampleSet, svmExamples, kernel, kernelType);
    }

    private double getFitness(double[] alphas, double[] ys, Kernel kernel) {
        double sum = 0.0d;
        int numberSV = 0;
        for (int i = 0; i < ys.length; i++) {
            sum += alphas[i];
            if (alphas[i] > 0) {
                numberSV++;
            }
        }

        double matrixSum = 0.0d;
        for (int i = 0; i < ys.length; i++) {
            if (alphas[i] == 0.0d) {
                continue;
            }
            for (int j = 0; j < ys.length; j++) {
                if (alphas[j] == 0.0d) {
                    continue;
                }
                matrixSum += alphas[i] * alphas[j] * ys[i] * ys[j] * kernel.calculate_K(i, j);
            }
        }
        return sum - 0.5d * matrixSum;
    }

    /**
     * Creates a new kernel of the given type. The kernel type has to be one out of KERNEL_DOT,
     * KERNEL_RADIAL, KERNEL_POLYNOMIAL, KERNEL_NEURAL, KERNEL_EPANECHNIKOV,
     * KERNEL_GAUSSIAN_COMBINATION, or KERNEL_MULTIQUADRIC.
     */
    public static Kernel createKernel(int kernelType) {
        switch (kernelType) {
            case KERNEL_DOT:
                return new KernelDot();
            case KERNEL_RADIAL:
                return new KernelRadial();
            case KERNEL_POLYNOMIAL:
                return new KernelPolynomial();
            case KERNEL_NEURAL:
                return new KernelNeural();
            case KERNEL_ANOVA:
                return new KernelAnova();
            /*case KERNEL_EPANECHNIKOV:
                return new KernelEpanechnikov();*/
            case KERNEL_GAUSSIAN_COMBINATION:
                return new KernelGaussianCombination();
            /*case KERNEL_MULTIQUADRIC:
                return new KernelMultiquadric();*/
            default:
                return new KernelDot();
        }
    }

    @Override
    public boolean supportsCapability(OperatorCapability lc) {
        if (lc == OperatorCapability.NUMERICAL_ATTRIBUTES) {
            return true;
        }
        if (lc == OperatorCapability.BINOMINAL_LABEL) {
            return true;
        }
        if (lc == OperatorCapability.NUMERICAL_LABEL) {
            return true;
        }
        if (lc == OperatorCapability.WEIGHTED_EXAMPLES) {
            return true;
        }
        if (lc == OperatorCapability.FORMULA_PROVIDER) {
            return true;
        }
        return false;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeCategory(PARAMETER_KERNEL_TYPE,
                I18N.getMessage("pio.AbstractMySVMLearner.kernel_type"), KERNEL_TYPES, 0);
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_GAMMA, I18N.getMessage("pio.AbstractMySVMLearner.kernel_gamma"), 0.0d,
                Double.POSITIVE_INFINITY, 1.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 1, 4));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_SIGMA1, I18N.getMessage("pio.AbstractMySVMLearner.kernel_sigma1"), 0.0d,
                Double.POSITIVE_INFINITY, 1.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 5, 6, 7));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_SIGMA2, I18N.getMessage("pio.AbstractMySVMLearner.kernel_sigma2"), 0.0d,
                Double.POSITIVE_INFINITY, 0.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 5));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_SIGMA3, I18N.getMessage("pio.AbstractMySVMLearner.kernel_sigma3"), 0.0d,
                Double.POSITIVE_INFINITY, 2.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 5));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_SHIFT, I18N.getMessage("pio.AbstractMySVMLearner.kernel_shift"), 0.0d,
                Double.POSITIVE_INFINITY, 1.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 7));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_DEGREE, I18N.getMessage("pio.AbstractMySVMLearner.kernel_degree"), 0.0d,
                Double.POSITIVE_INFINITY, 2);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 2, 4));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_A, I18N.getMessage("pio.AbstractMySVMLearner.kernel_a"), Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, 1.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 3));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_KERNEL_B, I18N.getMessage("pio.AbstractMySVMLearner.kernel_b"), Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, 0.0d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_KERNEL_TYPE, KERNEL_TYPES, false, 3));
        type.setExpert(false);
        types.add(type);
        types.add(new ParameterTypeInt(PARAMETER_KERNEL_CACHE, I18N.getMessage("pio.AbstractMySVMLearner.kernel_cache"), 0,
                Integer.MAX_VALUE, 200));
        type = new ParameterTypeDouble(PARAMETER_C,
                I18N.getMessage("pio.AbstractMySVMLearner.C"), -1,
                Double.POSITIVE_INFINITY, 0.0d);
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_CONVERGENCE_EPSILON, I18N.getMessage("pio.AbstractMySVMLearner.convergence_epsilon"), 0.0d,
                Double.POSITIVE_INFINITY, 1e-3);
        types.add(type);
        types.add(new ParameterTypeInt(PARAMETER_MAX_ITERATIONS, I18N.getMessage("pio.AbstractMySVMLearner.max_iterations"), 1, Integer.MAX_VALUE,
                100000));
        /*types.add(new ParameterTypeBoolean(PARAMETER_SCALE,
                I18N.getMessage("pio.AbstractMySVMLearner.scale"), true));*/

        // the following parameters are deprecated and switched to on in order to provide always
        // results on the outports and for loading old processes
        /*type = new ParameterTypeBoolean(PARAMETER_CALCULATE_WEIGHTS, I18N.getMessage("pio.AbstractMySVMLearner.calculate_weights"),
                true);
        type.setDeprecated();
        types.add(type);
        type = new ParameterTypeBoolean(PARAMETER_RETURN_OPTIMIZATION_PERFORMANCE,
                I18N.getMessage("pio.AbstractMySVMLearner.return_optimization_performance"), true);
        type.setDeprecated();
        types.add(type);*/
        return types;
    }
}
