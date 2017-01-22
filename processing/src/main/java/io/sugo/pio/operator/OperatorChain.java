package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;

import java.util.ArrayList;
import java.util.List;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "chainType", defaultImpl = ProcessRootOperator.class)
//@JsonSubTypes(value = {
//        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class)
//})
public abstract class OperatorChain extends Operator {

    @JsonProperty
    private List<ExecutionUnit> execUnits;

    public OperatorChain() {
        execUnits = new ArrayList<>();
    }

    public List<ExecutionUnit> getExecUnits() {
        return execUnits;
    }

    public void setExecUnits(List<ExecutionUnit> execUnits) {
        this.execUnits = execUnits;
    }

    @Override
    public ExecutionUnit getExecutionUnit() {
        final ExecutionUnit execUnit;
        if (execUnits.isEmpty()) {
            execUnit = new ExecutionUnit();
            execUnit.setEnclosingOperator(this);
            execUnits.add(execUnit);
        } else {
            execUnit = execUnits.get(0);
        }
        return execUnit;
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
        for (ExecutionUnit execUnit : execUnits) {
            execUnit.execute();
        }
    }

    @Override
    protected void registerOperator(OperatorProcess process) {
        super.registerOperator(process);
        for (ExecutionUnit execUnit : execUnits) {
            for (Operator child : execUnit.getOperators()) {
                execUnit.setEnclosingOperator(this);
                child.registerOperator(process);
                child.setEnclosingExecutionUnit(execUnit);
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

    @Override
    public void updateExecutionOrder() {
        for (ExecutionUnit unit : execUnits) {
            unit.updateExecutionOrder();
        }
    }
}
