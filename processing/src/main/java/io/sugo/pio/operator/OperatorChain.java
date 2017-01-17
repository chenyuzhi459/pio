package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "chainType", defaultImpl = ProcessRootOperator.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class)
})
public abstract class OperatorChain extends Operator {

    private ExecutionUnit[] execUnits;

    public OperatorChain(String... executionUnitNames) {
        execUnits = new ExecutionUnit[executionUnitNames.length];
        for (int i = 0; i < execUnits.length; i++) {
            execUnits[i] = new ExecutionUnit(this, executionUnitNames[i]);
        }
    }

    /**
     * This method returns an arbitrary implementation of {@link InputPorts} for inner sink port
     * initialization. Useful for adding an arbitrary implementation (e.g. changing port creation &
     * (dis)connection behavior, optionally by customized {@link InputPort} instances) by overriding
     * this method.
     *
     * @param portOwner
     *            The owner of the ports.
     * @return The {@link InputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
    protected InputPorts createInnerSinks(PortOwner portOwner) {
        return new InputPortsImpl(portOwner);
    }

    /**
     * This method returns an arbitrary implementation of {@link OutputPorts} for inner source port
     * initialization. Useful for adding an arbitrary implementation (e.g. changing port creation &
     * (dis)connection behavior, optionally by customized {@link OutputPort} instances) by
     * overriding this method.
     *
     * @param portOwner
     *            The owner of the ports.
     * @return The {@link OutputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
    protected OutputPorts createInnerSources(PortOwner portOwner) {
        return new OutputPortsImpl(portOwner);
    }

    @Override
    public void doWork() {
        for (ExecutionUnit subprocess : execUnits) {
            subprocess.execute();
        }
    }

    public ExecutionUnit getExecutionUnit(int index) {
        return execUnits[index];
    }

}
