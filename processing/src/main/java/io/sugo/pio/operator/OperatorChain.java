package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 */
public abstract class OperatorChain extends Operator implements Serializable {

    private List<ExecutionUnit> execUnits;

    public OperatorChain(String name, Collection<InputPort> inputPorts, Collection<OutputPort> outputPorts){
        super(name, inputPorts, outputPorts);
    }

    public void setExecUnits(List<ExecutionUnit> execUnits) {
        this.execUnits = execUnits;
        for (ExecutionUnit unit : execUnits) {
            unit.setEnclosingOperator(this);
        }
    }

    @JsonProperty("execUnits")
    public List<ExecutionUnit> getExecUnits() {
        return execUnits;
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
//    protected InputPorts createInnerSinks(PortOwner portOwner) {
//        return new InputPortsImpl(portOwner);
//    }

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
//    protected OutputPorts createInnerSources(PortOwner portOwner) {
//        return new OutputPortsImpl(portOwner);
//    }

    @Override
    public void doWork() {
        for (ExecutionUnit subprocess : execUnits) {
            subprocess.execute();
        }
    }

    public ExecutionUnit getSubprocess(int index) {
        return execUnits.get(index);
    }

    public int getNumberOfSubprocesses() {
        return execUnits.size();
    }

    /**
     * Returns an immutable view of all subprocesses
     */
    public List<ExecutionUnit> getSubprocesses() {
        return execUnits;
    }
}
