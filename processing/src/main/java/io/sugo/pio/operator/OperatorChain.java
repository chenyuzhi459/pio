package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "chainType", defaultImpl = ProcessRootOperator.class)
//@JsonSubTypes(value = {
//        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class)
//})
public abstract class OperatorChain extends Operator {

    @JsonProperty
    private ExecutionUnit[] execUnits;

    public OperatorChain(String... executionUnitNames) {
        execUnits = new ExecutionUnit[executionUnitNames.length];
        for (int i = 0; i < execUnits.length; i++) {
            execUnits[i] = new ExecutionUnit(this, executionUnitNames[i]);
        }
    }

    public ExecutionUnit[] getExecUnits() {
        return execUnits;
    }

    public void setExecUnits(ExecutionUnit[] execUnits) {
        this.execUnits = execUnits;
    }

    /**
     * This method returns an arbitrary implementation of {@link InputPorts} for inner sink port
     * initialization. Useful for adding an arbitrary implementation (e.g. changing port creation &
     * (dis)connection behavior, optionally by customized {@link InputPort} instances) by overriding
     * this method.
     *
     * @param portOwner The owner of the ports.
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
     * @param portOwner The owner of the ports.
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

    @Override
    protected void registerOperator(OperatorProcess process) {
        super.registerOperator(process);
        for (ExecutionUnit execUnit : execUnits) {
            for (Operator child : execUnit.getOperators()) {
                child.registerOperator(process);
            }
        }
    }

    /**
     * Unregisters this chain and all of its children from the given process.
     */
    @Override
    protected void unregisterOperator(OperatorProcess process) {
        super.unregisterOperator(process);
        for (ExecutionUnit execUnit : execUnits) {
            for (Operator child : execUnit.getOperators()) {
                child.unregisterOperator(process);
            }
        }
    }
}
