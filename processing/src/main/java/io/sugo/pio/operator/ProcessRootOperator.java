package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.ports.Port;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {

    public static final String TYPE = "root_operator";

    public ProcessRootOperator(
    ) {
        super("Main Process");
    }

    /**
     * Convenience backport method to get the results of a process.
     */
    public IOContainer getResults(boolean omitNullResults) {
        InputPorts inputPorts = getExecutionUnit(0).getInnerSinks();
        return inputPorts.createIOContainer(false, omitNullResults);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
