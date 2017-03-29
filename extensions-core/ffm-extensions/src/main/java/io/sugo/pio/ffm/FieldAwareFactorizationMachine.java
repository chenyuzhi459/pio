package io.sugo.pio.ffm;

import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.List;

/**
 */
public class FieldAwareFactorizationMachine extends Operator {

    /**
     * The parameter name for &quot;The number of training iterations used for the training.&quot;
     */
    public static final String PARAMETER_ITERATION = "iteration";

    public static final String PARAMETER_LEARNING_RATE = "learning_rate";

    public static final String PARAMETER_L2 = "lambda";

    public static final String PARAMETER_NORMALIZATION = "normalization";

    public static final String PARAMETER_RANDOM = "random";

    public static final String PARAMETER_LATENT_FACTOR_DIM = "latent_factor_dim";

    private final InputPort input = getInputPorts().createPort(PortConstant.EXAMPLE_SET, PortConstant.EXAMPLE_SET_DESC);
    private final OutputPort model = getOutputPorts().createPort(PortConstant.MODEL, PortConstant.MODEL_DESC);

    @Override
    public String getDefaultFullName() {
        return FieldAwareFactorizationMachine.class.getSimpleName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.fm;
    }

    @Override
    public String getDescription() {
        return FieldAwareFactorizationMachine.class.getSimpleName();
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

        ExampleSet exampleSet = input.getData(ExampleSet.class);

        new FieldAwareFactorizationMachineModel(exampleSet, 0, 0, latentFactorDim,null);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(PARAMETER_ITERATION, "The number of iterations used for training.",
                1, Integer.MAX_VALUE, 15));
        types.add(new ParameterTypeDouble(
                PARAMETER_LEARNING_RATE,
                "The learning rate determines by how much we change the weights at each step. May not be 0.",
                Double.MIN_VALUE, 1.0d, 0.1d));
        types.add(new ParameterTypeDouble(
                PARAMETER_L2,
                "The weight on l2 regularization.",
                0d, 1d, 0d));
        types.add(new ParameterTypeBoolean(PARAMETER_NORMALIZATION,
                "Whether to do instance-wise normalization",
                true
        ));
        types.add(new ParameterTypeBoolean(PARAMETER_RANDOM,
                "Whether to randomization training order of samples",
                true
        ));
        types.add(new ParameterTypeInt(PARAMETER_LATENT_FACTOR_DIM,
                "The size of latent factor dim",
                1,
                5,
                4
        ));
        return types;
    }
}
