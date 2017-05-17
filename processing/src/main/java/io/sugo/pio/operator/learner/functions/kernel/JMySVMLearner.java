package io.sugo.pio.operator.learner.functions.kernel;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMpattern;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMregression;
import io.sugo.pio.operator.performance.EstimatedPerformance;
import io.sugo.pio.operator.performance.PerformanceVector;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import io.sugo.pio.tools.RandomGenerator;

import java.util.List;


/**
 * This learner uses the Java implementation of the support vector machine <em>mySVM</em> by Stefan
 * R&uuml;ping. This learning method can be used for both regression and classification and provides
 * a fast algorithm and good results for many learning tasks.
 *
 * @rapidminer.reference Rueping/2000a
 * @rapidminer.reference Vapnik/98a
 * @rapidminer.index SVM
 */
public class JMySVMLearner extends AbstractMySVMLearner {

    private static final Logger logger = new Logger(JMySVMLearner.class);

    /**
     * The parameter name for &quot;Indicates if this learner should also return a performance
     * estimation.&quot;
     */
    public static final String PARAMETER_ESTIMATE_PERFORMANCE = "estimate_performance";

    /**
     * The parameter name for &quot;A factor for the SVM complexity constant for positive
     * examples&quot;
     */
    public static final String PARAMETER_L_POS = "L_pos";

    /**
     * The parameter name for &quot;A factor for the SVM complexity constant for negative
     * examples&quot;
     */
    public static final String PARAMETER_L_NEG = "L_neg";

    /**
     * The parameter name for &quot;Insensitivity constant. No loss if prediction lies this close to
     * true value&quot;
     */
    public static final String PARAMETER_EPSILON = "epsilon";

    /**
     * The parameter name for &quot;Epsilon for positive deviation only&quot;
     */
    public static final String PARAMETER_EPSILON_PLUS = "epsilon_plus";

    /**
     * The parameter name for &quot;Epsilon for negative deviation only&quot;
     */
    public static final String PARAMETER_EPSILON_MINUS = "epsilon_minus";

    /**
     * The parameter name for &quot;Adapts Cpos and Cneg to the relative size of the classes&quot;
     */
    public static final String PARAMETER_BALANCE_COST = "balance_cost";

    /**
     * The parameter name for &quot;Use quadratic loss for positive deviation&quot;
     */
    public static final String PARAMETER_QUADRATIC_LOSS_POS = "quadratic_loss_pos";

    /**
     * The parameter name for &quot;Use quadratic loss for negative deviation&quot;
     */
    public static final String PARAMETER_QUADRATIC_LOSS_NEG = "quadratic_loss_neg";

    public JMySVMLearner() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.JMySVMLearner.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.classification;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.JMySVMLearner.description");
    }

    @Override
    public int getSequence() {
        return 2;
    }

    /**
     * Indicates if the SVM is used for classification learning.
     */
    private boolean pattern = true;

    @Override
    public boolean shouldEstimatePerformance() {
        return getParameterAsBoolean(PARAMETER_ESTIMATE_PERFORMANCE);
    }

    @Override
    public boolean canEstimatePerformance() {
        return true;
    }

    /**
     * Returns the estimated performances of this SVM. Does only work for classification tasks.
     */
    @Override
    public PerformanceVector getEstimatedPerformance() throws OperatorException {
        if (!pattern) {
            throw new UserError(this, "pio.error.operator.learner_cannot_estimate",
                    this, "Cannot calculate leave one out estimation of error for regression tasks!");
        }
        double[] estVector = ((SVMpattern) getSVM()).getXiAlphaEstimation(getKernel());
        PerformanceVector pv = new PerformanceVector();
        pv.addCriterion(new EstimatedPerformance("xialpha_error", estVector[0], 1, true));
        pv.addCriterion(new EstimatedPerformance("xialpha_precision", estVector[1], 1, false));
        pv.addCriterion(new EstimatedPerformance("xialpha_recall", estVector[2], 1, false));
        pv.setMainCriterionName("xialpha_error");
        return pv;
    }

    @Override
    public AbstractMySVMModel createSVMModel(ExampleSet exampleSet, SVMExamples sVMExamples, Kernel kernel, int kernelType) {
        return new JMySVMModel(exampleSet, sVMExamples, kernel, kernelType);
    }

    @Override
    public SVMInterface createSVM(Attribute label, Kernel kernel, SVMExamples sVMExamples,
                                  io.sugo.pio.example.ExampleSet examples) throws OperatorException {
        if (label.isNominal()) {
            logger.info("JMySVMLearner create svm[SVMpattern] with nominal label.");
            collectLog("Create SVM with nominal label.");

            this.pattern = true;
            return new SVMpattern(this, kernel, sVMExamples, examples, RandomGenerator.getGlobalRandomGenerator());
        } else {
            logger.info("JMySVMLearner create svm[SVMregression] with none nominal label.");
            collectLog("Create SVM with none nominal label.");

            this.pattern = false;
            return new SVMregression(this, kernel, sVMExamples, examples,
                    RandomGenerator.getGlobalRandomGenerator());
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeDouble(PARAMETER_L_POS,
                I18N.getMessage("pio.JMySVMLearner.L_pos"),
                0, Double.POSITIVE_INFINITY, 1.0d));
        types.add(new ParameterTypeDouble(PARAMETER_L_NEG,
                I18N.getMessage("pio.JMySVMLearner.L_neg"),
                0, Double.POSITIVE_INFINITY, 1.0d));
        types.add(new ParameterTypeDouble(PARAMETER_EPSILON,
                I18N.getMessage("pio.JMySVMLearner.epsilon"),
                0.0d,
                Double.POSITIVE_INFINITY, 0.0d));
        types.add(new ParameterTypeDouble(PARAMETER_EPSILON_PLUS,
                I18N.getMessage("pio.JMySVMLearner.epsilon_plus"),
                0.0d,
                Double.POSITIVE_INFINITY, 0.0d));
        types.add(new ParameterTypeDouble(PARAMETER_EPSILON_MINUS,
                I18N.getMessage("pio.JMySVMLearner.epsilon_minus"),
                0.0d,
                Double.POSITIVE_INFINITY, 0.0d));
        /*types.add(new ParameterTypeBoolean(PARAMETER_BALANCE_COST,
                I18N.getMessage("pio.JMySVMLearner.balance_cost"),
                false));
        types.add(new ParameterTypeBoolean(PARAMETER_QUADRATIC_LOSS_POS,
                I18N.getMessage("pio.JMySVMLearner.quadratic_loss_pos"),
                false));
        types.add(new ParameterTypeBoolean(PARAMETER_QUADRATIC_LOSS_NEG,
                I18N.getMessage("pio.JMySVMLearner.quadratic_loss_neg"),
                false));*/

        // deprecated parameters
       /* ParameterType type = new ParameterTypeBoolean(PARAMETER_ESTIMATE_PERFORMANCE,
                I18N.getMessage("pio.JMySVMLearner.estimate_performance"),
                false);
        type.setDeprecated();
        types.add(type);*/
        return types;
    }

//    @Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getExampleSetInputPort(),
                JMySVMLearner.class, null);
    }
}
