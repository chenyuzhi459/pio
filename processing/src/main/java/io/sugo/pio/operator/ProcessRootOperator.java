package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {

    public static final String TYPE = "root_operator";

    public ProcessRootOperator(
            @JsonProperty("execUnits") List<ExecutionUnit> execUnits
    ) {
        super(null, execUnits, "root", null, null);
    }

    @JsonCreator
    public ProcessRootOperator(
            @JsonProperty("connections") List<Connection> connections,
            @JsonProperty("execUnits") List<ExecutionUnit> execUnits
    ) {
        super(connections, execUnits, "root", null, null);
    }

    /**
     * Convenience backport method to get the results of a process.
     */
    public IOContainer getResults() {
        List<InputPort> inputPorts = getExecutionUnit(0).getAllInputPorts();
        return createIOContainer(false, inputPorts);
    }

    public IOContainer createIOContainer(boolean onlyConnected, List<InputPort> inputPorts) {
        Collection<IOObject> output = new LinkedList<>();
        for (Port port : inputPorts) {
            if (!onlyConnected || port.isConnected()) {
                IOObject data = port.getAnyDataOrNull();
                output.add(data);
            }
        }
        return new IOContainer(output);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
