package sugo.io.pio.operator;

import sugo.io.pio.ports.InputPorts;
import sugo.io.pio.ports.InputPort;
import sugo.io.pio.ports.OutputPorts;
import sugo.io.pio.ports.OutputPort;
import sugo.io.pio.ports.PortOwner;
import sugo.io.pio.ports.impl.InputPortsImpl;
import sugo.io.pio.ports.impl.OutputPortsImpl;

import java.util.Arrays;
import java.util.List;

/**
 */
public abstract class OperatorChain extends Operator {

    private ExecutionUnit[] subprocesses;

    public OperatorChain(OperatorDescription description, String... subprocessNames) {
        super(description);
        subprocesses = new ExecutionUnit[subprocessNames.length];
        for (int i = 0; i < subprocesses.length; i++) {
            subprocesses[i] = new ExecutionUnit(this, subprocessNames[i]);
        }
    }

    /**
     * Adds the given operator at the given position. Please note that all operators (including the
     * disabled operators) are used for position calculations.
     */
    public final int addOperator(Operator operator, int index) {
        if (index < subprocesses.length) {
            subprocesses[index].addOperator(operator);
            return index;
        } else {
            throw new UnsupportedOperationException(
                    "addOperator() is no longer supported. Try getSubprocess(int).addOperator()");
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
        for (ExecutionUnit subprocess : subprocesses) {
            subprocess.execute();
        }
    }

    public ExecutionUnit getSubprocess(int index) {
        return subprocesses[index];
    }

    public int getNumberOfSubprocesses() {
        return subprocesses.length;
    }

    /** Returns an immutable view of all subprocesses */
    public List<ExecutionUnit> getSubprocesses() {
        return Arrays.asList(subprocesses);
    }
}
