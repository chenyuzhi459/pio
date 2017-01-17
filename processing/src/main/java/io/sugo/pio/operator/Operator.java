package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MDTransformer;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class),
})
public abstract class Operator implements ParameterHandler, Serializable {
    private final String name;

    /**
     * Parameters for this Operator.
     */
    private Parameters parameters = null;

    private Status status;

    /**
     * The list which stores the errors of this operator (parameter not set, wrong children number,
     * wrong IO).
     */
    private List<ProcessSetupError> errorList = Collections.synchronizedList(new LinkedList<ProcessSetupError>());

    // / SIMONS NEUERUNGEN
    private final PortOwner portOwner = new PortOwner() {
        @Override
        public Operator getOperator() {
            return Operator.this;
        }
    };

    private final InputPorts inputPorts;
    private final OutputPorts outputPorts;
    private final MDTransformer transformer = new MDTransformer(this);

    private ExecutionUnit enclosingExecutionUnit;

    /**
     * The {@link OperatorProgress} used to track progress during the execution of the operator.
     */
    private final OperatorProgress operatorProgress = new OperatorProgress(this);

    public Operator(String name) {
        this.name = name;


        this.inputPorts = createInputPorts(portOwner);
        this.outputPorts = createOutputPorts(portOwner);
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Status getStatus() {
        if(status == null){
            return Status.QUEUE;
        }
        return status;
    }

    public void setStatus(Status status) {
        if(status != null) {
            this.status = status;
        } else {
            this.status = Status.QUEUE;
        }
    }

    public InputPorts getInputPorts() {
        return inputPorts;
    }

    public OutputPorts getOutputPorts() {
        return outputPorts;
    }

    /**
     * This method returns an {@link InputPorts} object for port initialization. Useful for adding
     * an arbitrary implementation (e.g. changing port creation & (dis)connection behavior,
     * optionally by customized {@link InputPort} instances) by overriding this method.
     *
     * @param portOwner
     *            The owner of the ports.
     * @return The {@link InputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
    protected InputPorts createInputPorts(PortOwner portOwner) {
        return new InputPortsImpl(portOwner);
    }

    /**
     * This method returns an {@link OutputPorts} object for port initialization. Useful for adding
     * an arbitrary implementation (e.g. changing port creation & (dis)connection behavior,
     * optionally by customized {@link OutputPort} instances) by overriding this method.
     *
     * @param portOwner
     *            The owner of the ports.
     * @return The {@link OutputPorts} instance, never {@code null}.
     * @since 7.3.0
     */
    protected OutputPorts createOutputPorts(PortOwner portOwner) {
        return new OutputPortsImpl(portOwner);
    }


    public OperatorProcess getProcess() {
        Operator parent = getParent();
        if (parent == null) {
            return null;
        } else {
            return parent.getProcess();
        }
    }

    /**
     * Registers this operator in the given process. Please note that this might change the name of
     * the operator.
     */
    protected void registerOperator(OperatorProcess process) {
        if (process != null) {
            process.registerName(getName(), this);
        }
    }

    /** Deletes this operator removing it from the name map of the process. */
    protected void unregisterOperator(OperatorProcess process) {
        process.unregisterName(name);
    }

    /**
     * Performs the actual work of the operator and must be implemented by subclasses. Replaces the
     * old method <code>apply()</code>.
     */
    public void doWork() {
    }

    public void execute() {
        try {
            setStatus(Status.RUNNING);
            doWork();
            setStatus(Status.SUCCESS);
        } catch (OperatorException oe) {
            setStatus(Status.FAILED);
            throw oe;
        }
    }

    /**
     * Returns a list of <tt>ParameterTypes</tt> describing the parameters of this operator. The
     * default implementation returns an empty list if no input objects can be retained and special
     * parameters for those input objects which can be prevented from being consumed.
     * <p>
     * ATTENTION! This will create new parameterTypes. For calling already existing parameter types
     * use getParameters().getParameterTypes();
     */
    @Override
    public List<ParameterType> getParameterTypes() {
        return new LinkedList<>();
    }

    /**
     * Returns a collection of all parameters of this operator. If the parameters object has not
     * been created yet, it will now be created. Creation had to be moved out of constructor for
     * meta data handling in subclasses needing a port.
     */
    @Override
    public Parameters getParameters() {
        if (parameters == null) {
            // if not loaded already: do now
            parameters = new Parameters(getParameterTypes());
        }
        return parameters;
    }

    /**
     * Returns a single parameter retrieved from the {@link Parameters} of this Operator.
     */
    @Override
    public String getParameter(String key) {
        try {
            return getParameters().getParameter(key);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void setParameter(String key, String value) {
        getParameters().setParameter(key, value);
    }

    @Override
    public void setListParameter(String key, List<String[]> list) {
        getParameters().setParameter(key, ParameterTypeList.transformList2String(list));
    }

    /**
     * Returns a single named parameter and casts it to List. The list returned by this method
     * contains the user defined key-value pairs. Each element is a String array of length 2. The
     * first element is the key, the second the parameter value. The caller have to perform the
     * casts to the correct types himself.
     */
    @Override
    public List<String[]> getParameterList(String key) throws UndefinedParameterError {
        return ParameterTypeList.transformString2List(getParameter(key));
    }

    /**
     * Returns a single named parameter and casts it to String.
     */
    @Override
    public String getParameterAsString(String key) {
        return getParameter(key);
    }

    /**
     * Returns true iff the parameter with the given name is set. If no parameters object has been
     * created yet, false is returned. This can be used to break initialization loops.
     */
    @Override
    public boolean isParameterSet(String key) {
        return getParameters().isSet(key);
    }


    /**
     * Returns a single named parameter and casts it to char.
     */
    @Override
    public char getParameterAsChar(String key) {
        String parameterValue = getParameter(key);
        if (parameterValue.length() > 0) {
            return parameterValue.charAt(0);
        }
        return 0;
    }

    /**
     * Returns a single named parameter and casts it to int.
     */
    @Override
    public int getParameterAsInt(String key) {
        ParameterType type = this.getParameters().getParameterType(key);
        String value = getParameter(key);
        if (type != null) {
            if (type instanceof ParameterTypeCategory) {
                String parameterValue = value;
                try {
                    return Integer.valueOf(parameterValue);
                } catch (NumberFormatException e) {
                    ParameterTypeCategory categoryType = (ParameterTypeCategory) type;
                    return categoryType.getIndex(parameterValue);
                }
            }
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Expected integer but found '" + value + "'.");
        }
    }

    /**
     * Returns a single named parameter and casts it to long.
     */
    @Override
    public long getParameterAsLong(String key) {
        ParameterType type = this.getParameters().getParameterType(key);
        String value = getParameter(key);
        if (type != null) {
            if (type instanceof ParameterTypeCategory) {
                String parameterValue = value;
                try {
                    return Long.valueOf(parameterValue);
                } catch (NumberFormatException e) {
                    ParameterTypeCategory categoryType = (ParameterTypeCategory) type;
                    return categoryType.getIndex(parameterValue);
                }
            }
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Expected long but found '" + value + "'.");
        }
    }

    /**
     * Returns a single named parameter and casts it to double.
     */
    @Override
    public double getParameterAsDouble(String key) {
        String value = getParameter(key);
        if (value == null) {
            throw new RuntimeException("");
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Expected real number but found '" + value + "'.");
        }
    }

    /**
     * Returns a single named parameter and casts it to boolean. This method never throws an
     * exception since there are no non-optional boolean parameters.
     */
    @Override
    public boolean getParameterAsBoolean(String key) {
        try {
            return Boolean.valueOf(getParameter(key));
        } catch (Exception e) {
        }
        return false; // cannot happen
    }

    public void addError(ProcessSetupError error) {
        errorList.add(error);
    }

    /**
     * This method returns the {@link MDTransformer} object of this operator. This object will
     * process all meta data of all ports of this operator according to the rules registered to it.
     * This method can be used to get the transformer and register new Rules for
     * MetaDataTransformation for the ports using the
     * {@link MDTransformer#addRule(MDTransformationRule)}
     * method or one of it's more specialized sisters.
     */
    public final MDTransformer getTransformer() {
        return transformer;
    }

    /**
     * Returns the ExecutionUnit that contains this operator.
     */
    public final ExecutionUnit getExecutionUnit() {
        return enclosingExecutionUnit;
    }

    /**
     * If this method is called for perform the meta data transformation on this operator. It needs
     * the meta data on the input Ports to be already calculated.
     */
    public void transformMetaData() {
        getTransformer().transformMetaData();
    }

    /**
     */
    public boolean shouldAutoConnect(OutputPort outputPort) {
        return true;
    }

    /**
     * @see #shouldAutoConnect(OutputPort)
     */
    public boolean shouldAutoConnect(InputPort inputPort) {
        return true;
    }

    final protected void setEnclosingExecutionUnit(ExecutionUnit parent) {
        if (parent != null && this.enclosingExecutionUnit != null) {
            throw new IllegalStateException("Parent already set.");
        }
        this.enclosingExecutionUnit = parent;
    }

    /**
     * Returns the operator containing the enclosing process or null if this is the root operator.
     */
    public final OperatorChain getParent() {
        if (enclosingExecutionUnit != null) {
            return enclosingExecutionUnit.getEnclosingOperator();
        } else {
            return null;
        }
    }

    /**
     * Releases of any resources held by this operator due since its execution. In particular,
     * removes all hard references to IOObjects stored at the ports.
     */
    public void freeMemory() {
        getInputPorts().freeMemory();
        getOutputPorts().freeMemory();
    }

    /**
     * The {@link OperatorProgress} should be initialized when starting the operator execution by
     * setting the total amount of progress (which is {@link OperatorProgress#NO_PROGRESS} by
     * default) by calling {@link OperatorProgress#setTotal(int)}. Afterwards the progress can be
     * reported by calling {@link OperatorProgress#setCompleted(int)}. The progress will be reset
     * before the operator is being executed.
     *
     * @return the {@link OperatorProgress} to report progress during operator execution.
     * @since 7.0.0
     */
    public final OperatorProgress getProgress() {
        return operatorProgress;
    }
}
