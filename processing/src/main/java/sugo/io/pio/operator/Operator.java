package sugo.io.pio.operator;

import sugo.io.pio.Process;
import sugo.io.pio.ports.InputPort;
import sugo.io.pio.ports.OutputPort;
import sugo.io.pio.ports.InputPorts;
import sugo.io.pio.ports.OutputPorts;
import sugo.io.pio.ports.PortOwner;
import sugo.io.pio.ports.impl.InputPortsImpl;
import sugo.io.pio.ports.impl.OutputPortsImpl;

/**
 */
public abstract class Operator {
    private String name;

    private OperatorDescription operatorDescription = null;

    private boolean enabled = true;

    // / SIMONS NEUERUNGEN
    private final PortOwner portOwner = new PortOwner() {
        @Override
        public Operator getOperator() {
            return Operator.this;
        }
    };

    private final InputPorts inputPorts;
    private final OutputPorts outputPorts;

    private ExecutionUnit enclosingExecutionUnit;

    public Operator(OperatorDescription description) {
        this.operatorDescription = description;
        this.name = description.getKey();

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

    /** Returns the ExecutionUnit that contains this operator. */
    public final ExecutionUnit getExecutionUnit() {
        return enclosingExecutionUnit;
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
     * Releases of any resources held by this operator due since its execution. In particular,
     * removes all hard references to IOObjects stored at the ports.
     */
    public void freeMemory() {
        getInputPorts().freeMemory();
        getOutputPorts().freeMemory();
    }
}
