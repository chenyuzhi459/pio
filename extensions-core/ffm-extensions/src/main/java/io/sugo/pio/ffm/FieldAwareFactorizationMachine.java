package io.sugo.pio.ffm;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;

import java.util.List;

import static io.sugo.pio.ports.PortType.EXAMPLE_SET_INPUT;

/**
 */
public class FieldAwareFactorizationMachine extends Operator {

    /**
     * The parameter name for &quot;The number of training iterations used for the training.&quot;
     */
    public static final String PARAMETER_ITERATION = "iteration";

    public static final String PARAMETER_NORMALIZATION = "normalization";

    private final InputPort input = getInputPorts().createPort(EXAMPLE_SET_INPUT);
    private final OutputPort model = getOutputPorts().createPort(PortType.MODEL);

    @Override
    public String getDefaultFullName() {
        return FieldAwareFactorizationMachine.class.getSimpleName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return FieldAwareFactorizationMachine.class.getSimpleName();
    }

    @Override
    public void doWork() throws OperatorException {
        // iteration
        int iteration = getParameterAsInt(PARAMETER_ITERATION);
        boolean normalization = getParameterAsBoolean(PARAMETER_NORMALIZATION);


    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = null;
        type = new ParameterTypeInt(PARAMETER_ITERATION, "The number of iterations used for training.",
                1, Integer.MAX_VALUE, 500);
        type.setExpert(false);
        types.add(type);
        return types;
    }
}
