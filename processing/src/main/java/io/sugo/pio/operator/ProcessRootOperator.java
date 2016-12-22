package io.sugo.pio.operator;

import io.sugo.pio.Process;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPorts;

import java.util.LinkedList;
import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {
    /** The process which is connected to this process operator. */
    private Process process;

    public ProcessRootOperator(OperatorDescription description) {
        this(description, null);
    }

    public ProcessRootOperator(OperatorDescription description, Process process) {
        super(description, "Main Process");
        setProcess(process);
        rename("Root");
    }

    /**
     * Convenience backport method to get the results of a process.
     *
     * @param omitNullResults
     *            if set to <code>false</code> the returned {@link IOContainer} will contain
     *            <code>null</code> values for empty results instead of omitting them.
     */
    public IOContainer getResults(boolean omitNullResults) {
        InputPorts innerSinks = getSubprocess(0).getInnerSinks();
        return innerSinks.createIOContainer(false, omitNullResults);
    }

    /** Sets the process. */
    public void setProcess(Process process) {
        this.process = process;
        registerOperator(this.process);
    }

    /**
     * Returns the process of this operator if available. Overwrites the method from the superclass.
     */
    @Override
    public Process getProcess() {
        return process;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        return types;
    }
}
