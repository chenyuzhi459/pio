package io.sugo.pio.ffm;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;

import java.util.List;

import static io.sugo.pio.ports.PortType.EXAMPLE_SET_INPUT;

/**
 */
public class FactorizationMachine extends Operator {
    private final InputPort input = getInputPorts().createPort(EXAMPLE_SET_INPUT);
    private final OutputPort model = getOutputPorts().createPort(PortType.MODEL);

    @Override
    public String getDefaultFullName() {
        return FactorizationMachine.class.getSimpleName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return FactorizationMachine.class.getSimpleName();
    }

    @Override
    public void doWork() throws OperatorException {

    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
