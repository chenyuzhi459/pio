package io.sugo.pio.operator.learner;


import com.metamx.common.logger.Logger;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.error.ProcessSetupError;
import io.sugo.pio.operator.performance.PerformanceVector;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.tools.ParameterService;
import io.sugo.pio.tools.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A <tt>Learner</tt> is an operator that encapsulates the learning step of a machine learning
 * method. New learning schemes should extend this class to support the same parameters as other
 * RapidMiner learners. The main purpose of this class is to perform some compatibility checks.
 *
 */
public abstract class AbstractLearner extends Operator implements Learner {

    private static final Logger logger = new Logger(AbstractLearner.class);

    private final InputPort exampleSetInput = getInputPorts().createPort(PortConstant.TRAINING_SET, PortConstant.TRAINING_SET_DESC);
    private final OutputPort modelOutput = getOutputPorts().createPort(PortConstant.MODEL, PortConstant.MODEL_DESC);
    private final OutputPort performanceOutput = getOutputPorts().createPort(PortConstant.ESTIMATED_PERFORMANCE,
            PortConstant.ESTIMATED_PERFORMANCE_DESC, canEstimatePerformance());
    private final OutputPort weightsOutput = getOutputPorts().createPort(PortConstant.WEIGHTS, PortConstant.WEIGHTS_DESC, canCalculateWeights());
    private final OutputPort exampleSetOutput = getOutputPorts().createPort(PortConstant.EXAMPLE_SET, PortConstant.EXAMPLE_SET_DESC);

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(modelOutput.getAnyDataOrNull());
        ioObjects.add(performanceOutput.getAnyDataOrNull());
        ioObjects.add(weightsOutput.getAnyDataOrNull());
        ioObjects.add(exampleSetOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    /**
     * Creates a new abstract
     */
    public AbstractLearner() {
        exampleSetInput.addPrecondition(new LearnerPrecondition(this, exampleSetInput));
        getTransformer().addRule(
                new GeneratePredictionModelTransformationRule(exampleSetInput, modelOutput, getModelClass()));
        getTransformer().addRule(new GenerateNewMDRule(performanceOutput, new MetaData(PerformanceVector.class)) {

            @Override
            public MetaData modifyMetaData(MetaData unmodifiedMetaData) {
                if (canEstimatePerformance()) {
                    return unmodifiedMetaData;
                } else {
                    return null;
                }
            }
        });
        getTransformer().addRule(new GenerateNewMDRule(weightsOutput, new MetaData(AttributeWeights.class)) {

            @Override
            public MetaData modifyMetaData(MetaData unmodifiedMetaData) {
                if (canCalculateWeights()) {
                    return unmodifiedMetaData;
                } else if (weightsOutput.isConnected()) {
                    weightsOutput.addError(getWeightCalculationError(weightsOutput));
                }
                return null;
            }

        });
        getTransformer().addRule(new PassThroughRule(exampleSetInput, exampleSetOutput, false));
    }

    @Override
    public boolean shouldAutoConnect(OutputPort outputPort) {
        if (outputPort == performanceOutput) {
            return shouldEstimatePerformance();
        } else if (outputPort == weightsOutput) {
            return shouldCalculateWeights();
        } else if (outputPort == exampleSetOutput) {
            return getParameterAsBoolean("keep_example_set");
        } else {
            return super.shouldAutoConnect(outputPort);
        }
    }

    /**
     * Helper method in case this operator is constructed anonymously. Assigns the example set to
     * the input port and returns the model.
     */
    public Model doWork(ExampleSet exampleSet) throws OperatorException {
        exampleSetInput.receive(exampleSet);
        doWork();
        return modelOutput.getData(Model.class);
    }

    /**
     * Returns the weights (if computed, after one of the doWork()} methods has been called.
     *
     * @throws OperatorException
     */
    public AttributeWeights getWeights() throws OperatorException {
        return weightsOutput.getData(AttributeWeights.class);
    }

    /**
     * This method might be overridden from subclasses in order to specify exactly which model class
     * they use. This is to ensure the proper postprocessing of some models like KernelModels
     * (SupportVectorCounter) or TreeModels (Rule generation)
     */
    public Class<? extends PredictionModel> getModelClass() {
        return PredictionModel.class;
    }

    /**
     * Trains a model using an ExampleSet from the input. Uses the method learn(ExampleSet).
     */
    @Override
    public void doWork() throws OperatorException {
        ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
        logger.info("Abstract learner begin to learn through example set[%s], which has size[%d]",
                exampleSet.getName(), exampleSet.size());

        // some checks
        if (exampleSet.getAttributes().getLabel() == null) {
            throw new UserError(this, "pio.error.operator.exampleset_miss_label");
        }
        if (exampleSet.getAttributes().size() == 0) {
            throw new UserError(this, "pio.error.operator.exampleset_no_attributes");
        }
        if (exampleSet.size() == 0) {
            throw new UserError(this, "pio.error.operator.exampleset_empty");
        }

        // check capabilities and produce errors if they are not fulfilled
        CapabilityCheck check = new CapabilityCheck(this, Tools.booleanValue(
                ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GENERAL_CAPABILITIES_WARN), true)
                || onlyWarnForNonSufficientCapabilities());
        check.checkLearnerCapabilities(this, exampleSet);

        Model model = learn(exampleSet);
        modelOutput.deliver(model);

        // weights must be calculated _after_ learning
        // we are still asking for shouldCalcluate weights since, e.g., SVMWeighting needs an
        // anonymous
        // learner whose weightOutputs is not connected, so only checking for
        // weightsOutput.isConnected()
        // is not sufficient.
        if (canCalculateWeights() && weightsOutput.isConnected()) { // || shouldCalculateWeights())
            // {
            AttributeWeights weights = getWeights(exampleSet);
            if (weights != null) {
                weightsOutput.deliver(weights);
            }
        }

        if (canEstimatePerformance() && performanceOutput.isConnected()) {
            PerformanceVector perfVector = null;
            if (shouldDeliverOptimizationPerformance()) {
                perfVector = getOptimizationPerformance();
            } else {
                perfVector = getEstimatedPerformance();
            }
            performanceOutput.deliver(perfVector);
        }

        exampleSetOutput.deliver(exampleSet);

        logger.info("Abstract learner learn through example set[%s] and deliver to the next operator finished.",
                exampleSet.getName(), exampleSet.size());
    }

    /**
     * Returns true if the user wants to estimate the performance (depending on a parameter). In
     * this case the method getEstimatedPerformance() must also be overridden and deliver the
     * estimated performance. The default implementation returns false.
     *
     * @deprecated This method is not used any longer. Performance is estimated iff
     * {@link #canEstimatePerformance()} returns true and the corresponding port is
     * connected.
     */
    @Override
    @Deprecated
    public boolean shouldEstimatePerformance() {
        return false;
    }

    /**
     * Returns true if this learner is capable of estimating its performance. If this returns true,
     * a port will be created and {@link #getEstimatedPerformance()} will be called if this port is
     * connected.
     */
    public boolean canEstimatePerformance() {
        return false;
    }

    /**
     * Returns true if the user wants to calculate feature weights (depending on a parameter). In
     * this case the method getWeights() must also be overriden and deliver the calculated weights.
     * The default implementation returns false.
     *
     * @deprecated This method is not used any longer. Weights are computed iff
     * {@link #canCalculateWeights()} returns true and the corresponding port is
     * connected.
     */
    @Override
    @Deprecated
    public boolean shouldCalculateWeights() {
        return false;
    }

    /**
     * Returns true if this learner is capable of computing attribute weights. If this method
     * returns true, also override {@link #getWeights(ExampleSet)}
     */
    public boolean canCalculateWeights() {
        return false;
    }

    public MetaDataError getWeightCalculationError(OutputPort weightPort) {
        return new SimpleMetaDataError(ProcessSetupError.Severity.ERROR, weightPort,
                "pio.error.metadata.parameters.incompatible_for_delivering",
                "AttributeWeights");
    }

    /**
     * Returns true if the user wants to deliver the performance of the original optimization
     * problem. Since many learners are basically optimization procedures for a certain type of
     * objective function the result of this procedure might also be of interest in some cases.
     */
    public boolean shouldDeliverOptimizationPerformance() {
        return false;
    }

    /**
     * Returns the estimated performance. Subclasses which supports the capability to estimate the
     * learning performance must override this method. The default implementation throws an
     * exception.
     */
    @Override
    public PerformanceVector getEstimatedPerformance() throws OperatorException {
        throw new UserError(this, "pio.error.operator.learner_cannot_estimate",
                getName(), "estimation of performance not supported.");
    }

    /**
     * Returns the resulting performance of the original optimization problem. Subclasses which
     * supports the capability to deliver this performance must override this method. The default
     * implementation throws an exception.
     */
    public PerformanceVector getOptimizationPerformance() throws OperatorException {
        throw new UserError(this, "pio.error.operator.learner_cannot_estimate",
                getName(), "delivering the original optimization performance is not supported.");
    }

    /**
     * Returns the calculated weight vectors. Subclasses which supports the capability to calculate
     * feature weights must override this method. The default implementation throws an exception.
     */
    @Override
    public AttributeWeights getWeights(ExampleSet exampleSet) throws OperatorException {
        throw new UserError(this, "pio.error.operator.learner_cannot_weights",
                getName(), "calculation of weights not supported.");
    }

    public boolean onlyWarnForNonSufficientCapabilities() {
        return false;
    }

    public InputPort getExampleSetInputPort() {
        return this.exampleSetInput;
    }

}
