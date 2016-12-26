package io.sugo.pio.operator;

import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.OutputPorts;
import io.sugo.pio.ports.PortOwner;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import io.sugo.pio.ports.InputPort;

import java.util.Arrays;
import java.util.List;

/**
 */
public abstract class OperatorChain extends Operator {

    private ExecutionUnit[] execUnits;

    public void setExecUnits(ExecutionUnit[] execUnits) {
        this.execUnits = execUnits;
        for (int i = 0; i < execUnits.length; i++) {
            execUnits[i].setEnclosingOperator(this);
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

    public ExecutionUnit getSubprocess(int index) {
        return execUnits[index];
    }

    public int getNumberOfSubprocesses() {
        return execUnits.length;
    }

    /** Returns an immutable view of all subprocesses */
    public List<ExecutionUnit> getSubprocesses() {
        return Arrays.asList(execUnits);
    }
}
