package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPorts;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {

    public static final String TYPE = "root";

    /** The process which is connected to this process operator. */
    private Process process;

    @JsonCreator
    public ProcessRootOperator(
            @JsonProperty("execUnits") List<ExecutionUnit> execUnits
    ) {
        rename(TYPE);
        setExecUnits(execUnits);
    }

//    @Override
//    public String getType() {
//        return TYPE;
//    }

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
