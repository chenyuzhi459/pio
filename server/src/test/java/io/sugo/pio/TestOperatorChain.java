package io.sugo.pio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sugo.pio.operator.*;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@JsonTypeName("test")
public final class TestOperatorChain extends OperatorChain {

    public static final String TYPE = "test";

    /** The process which is connected to this process operator. */
    private OperatorProcess process;

    @JsonCreator
    public TestOperatorChain(
            @JsonProperty("execUnits") List<ExecutionUnit> execUnits
    ) {
        super("root", null, null);
        setExecUnits(execUnits);
    }

    public IOContainer getResults(boolean omitNullResults) {
        List<InputPort> inputPorts = getSubprocess(0).getAllInputPorts();
        return createIOContainer(false, omitNullResults, inputPorts);
    }

    public IOContainer createIOContainer(boolean onlyConnected, boolean omitEmptyResults, List<InputPort> inputPorts) {
        Collection<IOObject> output = new LinkedList<>();
        for (Port port : inputPorts) {
            if (!onlyConnected || port.isConnected()) {
                IOObject data = port.getAnyDataOrNull();
                if (omitEmptyResults) {
                    if (data != null) {
                        output.add(data);
                    }
                } else {
                    output.add(data);
                }
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
