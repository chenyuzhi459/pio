package io.sugo.pio.ffm;

import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.List;


/**
 */
public class FactorizationMachine extends Operator {
    private final InputPort input = getInputPorts().createPort(PortConstant.EXAMPLE_SET, PortConstant.EXAMPLE_SET_DESC);
    private final OutputPort model = getOutputPorts().createPort(PortConstant.MODEL, PortConstant.MODEL_DESC);


    @Override
    public String getDefaultFullName() {
        return FactorizationMachine.class.getSimpleName();
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.fm;
    }

    @Override
    public String getDescription() {
        return FactorizationMachine.class.getSimpleName();
    }

    @Override
    public int getSequence() {
        return 0;
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
