package io.sugo.pio.ffm;

import com.metamx.common.logger.Logger;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.learner.AbstractLearner;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.tools.Ontology;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class FieldAwareFactorizationMachine extends Operator {

    private static final Logger logger = new Logger(FieldAwareFactorizationMachine.class);

    /**
     * The parameter name for &quot;The number of training iterations used for the training.&quot;
     */
    public static final String PARAMETER_ITERATION = "iteration";

    public static final String PARAMETER_LEARNING_RATE = "learning_rate";

    public static final String PARAMETER_L2 = "lambda";

    public static final String PARAMETER_NORMALIZATION = "normalization";

    public static final String PARAMETER_RANDOM = "random";

    public static final String PARAMETER_LATENT_FACTOR_DIM = "latent_factor_dim";

    private final InputPort input = getInputPorts().createPort(PortConstant.TRAINING_EXAMPLES, PortConstant.TRAINING_EXAMPLES_DESC);
    private final OutputPort modelOutput = getOutputPorts().createPort(PortConstant.MODEL, PortConstant.MODEL_DESC);

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.FieldAwareFactorizationMachine.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.classification;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.FieldAwareFactorizationMachine.description");
    }

    @Override
    public int getSequence() {
        return 1;
    }

    @Override
    public void doWork() throws OperatorException {
        // iteration
        int iteration = getParameterAsInt(PARAMETER_ITERATION);
        boolean normalization = getParameterAsBoolean(PARAMETER_NORMALIZATION);
        boolean random = getParameterAsBoolean(PARAMETER_RANDOM);
        double lr = getParameterAsDouble(PARAMETER_LEARNING_RATE);
        double l2 = getParameterAsDouble(PARAMETER_L2);
        int latentFactorDim = getParameterAsInt(PARAMETER_LATENT_FACTOR_DIM);

        String firstClassName = null;
        String secondClassName = null;
        ExampleSet trainExampleSet = input.getData(ExampleSet.class);
        Attribute label = trainExampleSet.getAttributes().getLabel();
        Attribute workingLabel = label;
        if (label == null) {
            throw new UserError(this, "pio.error.operator.exampleset_miss_label");
        }

        if (label.isNominal()) {
            logger.info("FFM of nominal label.");

            if (label.getMapping().size() == 2) {
                firstClassName = label.getMapping().getNegativeString();
                secondClassName = label.getMapping().getPositiveString();

                int firstIndex = label.getMapping().getNegativeIndex();

                workingLabel = AttributeFactory.createAttribute("ffm_label", Ontology.REAL);
                trainExampleSet.getExampleTable().addAttribute(workingLabel);

                for (Example example : trainExampleSet) {
                    double index = example.getValue(label);
                    if (index == firstIndex) {
                        example.setValue(workingLabel, 0.0d);
                    } else {
                        example.setValue(workingLabel, 1.0d);
                    }
                }

                trainExampleSet.getAttributes().setLabel(workingLabel);
            }
        }

        FFMProblem trainProblem = FFMProblem.convertExampleSet(trainExampleSet);

        FFMParameter param = FFMParameter.defaultParameter();
        param.eta = (float) lr;
        param.lambda = (float) l2;
        param.n_iters = iteration;
        param.k = latentFactorDim;
        param.normalization = normalization;
        param.random = random;

        FFMModel ffmModel = new FFMModel().train(trainProblem, null, param);

        FieldAwareFactorizationMachineModel model =
                new FieldAwareFactorizationMachineModel(trainExampleSet, ffmModel.n, ffmModel.m,
                        ffmModel.k, ffmModel.W, ffmModel.normalization, firstClassName, secondClassName);

        modelOutput.deliver(model);
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(modelOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(PARAMETER_ITERATION, //"The number of iterations used for training.",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.iterations"),
                1, Integer.MAX_VALUE, 15));
        types.add(new ParameterTypeDouble(
                PARAMETER_LEARNING_RATE,
                //"The learning rate determines by how much we change the weights at each step. May not be 0.",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.learning_rate"),
                Double.MIN_VALUE, 1.0d, 0.1d));
        types.add(new ParameterTypeDouble(
                PARAMETER_L2,
//                "The weight on l2 regularization.",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.l2_regularization"),
                0d, 1d, 0d));
        types.add(new ParameterTypeBoolean(PARAMETER_NORMALIZATION,
//                "Whether to do instance-wise normalization",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.normalization"),
                true
        ));
        types.add(new ParameterTypeBoolean(PARAMETER_RANDOM,
//                "Whether to randomization training order of samples",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.random_samples"),
                true
        ));
        types.add(new ParameterTypeInt(PARAMETER_LATENT_FACTOR_DIM,
//                "The size of latent factor dim",
                I18N.getMessage("pio.FieldAwareFactorizationMachine.latent_factor_dim"),
                1,
                5,
                4
        ));
        return types;
    }
}
