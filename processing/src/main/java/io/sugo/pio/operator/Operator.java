package io.sugo.pio.operator;

import io.sugo.pio.Process;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MDTransformer;

import java.util.LinkedList;
import java.util.List;

/**
 */
public abstract class Operator implements ParameterHandler {
    private String name;

    private boolean enabled = true;

    /** Parameters for this Operator. */
    private Parameters parameters = null;

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

    public Operator() {
        this.inputPorts = createInputPorts(portOwner);
        this.outputPorts = createOutputPorts(portOwner);
    }

    public String getName() {
        return name;
    }

    /**
     * This method simply sets the name to the given one. Please note that it is not checked if the
     * name was already used in the process. Please use the method {@link #rename(String)} for usual
     * renaming.
     */
    private final void setName(String newName) {
        this.name = newName;
    }

    /**
     * This method unregisters the old name if this operator is already part of a {@link Process}.
     * Afterwards, the new name is set and registered in the process. Please note that the name
     * might be changed during registering in order to ensure that each operator name is unique in
     * its process. The new name will be returned.
     */
    public final String rename(String newName) {
        Process process = getProcess();
        if (process != null) {
            process.unregisterName(this.name);
            this.name = process.registerName(newName, this);
        } else {
            this.name = newName;
        }
        return this.name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the process of this operator by asking the parent operator. If the operator itself
     * and all of its parents are not part of an process, this method will return null. Please note
     * that some operators (e.g. ProcessLog) must be part of an process in order to work properly.
     */
    public Process getProcess() {
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
    protected void registerOperator(Process process) {
        if (process != null) {
            setName(process.registerName(getName(), this));
        }
    }

    /**
     * Implement this method in subclasses.
     *
     * @deprecated use doWork()
     */
    @Deprecated
    public IOObject[] apply() {
        throw new UnsupportedOperationException("apply() is deprecated. Implement doWork().");
    }


    /**
     * Performs the actual work of the operator and must be implemented by subclasses. Replaces the
     * old method <code>apply()</code>.
     */
    public void doWork() {
    }

    public void execute() {
        if (isEnabled()) {
            doWork();
        }
    }


    /**
     * The default implementation returns an input description that consumes the input IOObject
     * without a user parameter. Subclasses may override this method to allow other input handling
     * behaviors.
     *
     * @deprecated
     */
    @Deprecated
    protected InputDescription getInputDescription(Class<?> inputClass) {
        return new InputDescription(inputClass);
    }

    /**
     * Returns a list of <tt>ParameterTypes</tt> describing the parameters of this operator. The
     * default implementation returns an empty list if no input objects can be retained and special
     * parameters for those input objects which can be prevented from being consumed.
     *
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
    public String getParameter(String key)  {
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

    /** Returns a single named parameter and casts it to String. */
    @Override
    public String getParameterAsString(String key) {
        return getParameter(key);
    }

    /** Returns a single named parameter and casts it to char. */
    @Override
    public char getParameterAsChar(String key) {
        String parameterValue = getParameter(key);
        if (parameterValue.length() > 0) {
            return parameterValue.charAt(0);
        }
        return 0;
    }

    /** Returns a single named parameter and casts it to int. */
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

    /** Returns a single named parameter and casts it to long. */
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

    /** Returns a single named parameter and casts it to double. */
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


    /**
     * This method returns the {@link InputPorts} object that gives access to all defined
     * {@link InputPort}s of this operator. This object can be used to create a new
     * {@link InputPort} for an operator using one of the {@link InputPorts#createPort(String)}
     * methods.
     */
    public final InputPorts getInputPorts() {
        return inputPorts;
    }

    /**
     * This method returns the {@link OutputPorts} object that gives access to all defined
     * {@link OutputPort}s of this operator. This object can be used to create a new
     * {@link OutputPort} for an operator using one of the {@link OutputPorts#createPort(String)}
     * methods.
     */
    public final OutputPorts getOutputPorts() {
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

    /** Returns the ExecutionUnit that contains this operator. */
    public final ExecutionUnit getExecutionUnit() {
        return enclosingExecutionUnit;
    }

    /**
     * If this method is called for perform the meta data transformation on this operator. It needs
     * the meta data on the input Ports to be already calculated.
     */
    public void transformMetaData() {
        if (!isEnabled()) {
            return;
        }
        getInputPorts().checkPreconditions();
        getTransformer().transformMetaData();
    }

    /**
     * By default, all ports will be auto-connected by
     * {@link ExecutionUnit#autoWire(boolean, boolean)}. Optional outputs were
     * handled up to version 4.4 by parameters. From 5.0 on, optional outputs are computed iff the
     * corresponding port is connected. For backward compatibility, operators can check if we should
     * auto-connect a port by overriding this method (e.g. by checking a deprecated parameter).
     * TODO: Remove in later versions
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

    final protected void setEnclosingProcess(ExecutionUnit parent) {
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
     * This method is called before auto-wiring an operator. Operators can reorder outputs in order
     * to influence how subsequent operators are wired. This is only necessary for legacy operators
     * like IOConsumer or IOSelector. Don't override this method for new operators.
     */
    protected LinkedList<OutputPort> preAutoWire(LinkedList<OutputPort> readyOutputs) {
        return readyOutputs;
    }

    /**
     * Releases of any resources held by this operator due since its execution. In particular,
     * removes all hard references to IOObjects stored at the ports.
     */
    public void freeMemory() {
        getInputPorts().freeMemory();
        getOutputPorts().freeMemory();
    }
}
