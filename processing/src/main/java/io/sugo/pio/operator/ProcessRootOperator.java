package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.ports.InputPorts;
import io.sugo.pio.tools.ParameterService;

import java.util.Iterator;
import java.util.List;

/**
 */
public final class ProcessRootOperator extends OperatorChain {
    public static final String TYPE = "root_operator";

    /**
     * The property name for &quot;The default random seed (-1: random random seed).&quot;
     */
    public static final String PROPERTY_PIO_GENERAL_RANDOMSEED = "pio.general.randomseed";

    public static final String PARAMETER_RANDOM_SEED = "random_seed";
    private OperatorProcess operatorProcess;

    public ProcessRootOperator() {
    }

    @JsonIgnore
    @Override
    public String getName() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getDefaultFullName() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getDescription() {
        return null;
    }

    @JsonIgnore
    @Override
    public OperatorGroup getGroup() {
        return null;
    }

    @JsonIgnore
    @Override
    public Integer getxPos() {
        return 0;
    }

    @JsonIgnore
    @Override
    public Integer getyPos() {
        return 0;
    }

    @JsonIgnore
    @Override
    public String getErrorMsg() {
        return null;
    }

    /**
     * Called at the beginning of the process. Notifies all listeners and the children operators
     * (super method).
     */
    @Override
    public void processStarts() throws OperatorException {
        super.processStarts();
        /*Iterator i = getListenerListCopy().iterator();
        while (i.hasNext()) {
            ((ProcessListener) i.next()).processStarts(this.process);
        }*/
    }

    /**
     * Convenience backport method to get the results of a process.
     */
    public IOContainer getResults(boolean omitNullResults) {
        InputPorts inputPorts = getExecutionUnit().getInnerSinks();
        return inputPorts.createIOContainer(false, omitNullResults);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        int seed = 2001;
        String seedProperty = ParameterService.getParameterValue(PROPERTY_PIO_GENERAL_RANDOMSEED);
        try {
            if (seedProperty != null) {
                seed = Integer.parseInt(seedProperty);
            }
        } catch (NumberFormatException e) {
        }
        types.add(new ParameterTypeInt(PARAMETER_RANDOM_SEED,
                "Global random seed for random generators (-1 for initialization by system time).", Integer.MIN_VALUE,
                Integer.MAX_VALUE, seed));
        return types;
    }

    public void setProcess(OperatorProcess operatorProcess) {
        this.operatorProcess = operatorProcess;
        registerOperator(this.operatorProcess);
    }

    @Override
    public OperatorProcess getProcess() {
        return operatorProcess;
    }
}
