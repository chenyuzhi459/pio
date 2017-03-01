package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.nio.model.ParseException;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.*;
import io.sugo.pio.ports.impl.InputPortsImpl;
import io.sugo.pio.ports.impl.OutputPortsImpl;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MDTransformer;
import io.sugo.pio.ports.metadata.SimpleProcessSetupError;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "operatorType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ProcessRootOperator.TYPE, value = ProcessRootOperator.class)
})
public abstract class Operator implements ParameterHandler, Serializable {
    private String name;
    protected String fullName;
    private Integer xPos;
    private Integer yPos;
    /**
     * Parameters for this Operator.
     */
    private Parameters parameters = null;

    private Status status;
    private boolean isRunning = false;
    /**
     * The values for this operator. The current value of a Value can be asked by the
     * ProcessLogOperator.
     */
    private final Map<String, Value> valueMap = new TreeMap<>();
    /**
     * May differ from {@link #applyCount} if parallellized.
     */
    private int applyCountAtLastExecution = -1;
    /**
     * Number of times the operator was applied.
     */
    private AtomicInteger applyCount = new AtomicInteger();

    private transient final Logger logger = Logger.getLogger(Operator.class.getName());

//    private OperatorVersion compatibilityLevel;

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

    private final Object userDataLock = new Object();
    private Map<String, UserData<Object>> userData;

    public Operator() {
        this.inputPorts = createInputPorts(portOwner);
        this.outputPorts = createOutputPorts(portOwner);
        addValue(new ValueDouble("applycount", "The number of times the operator was applied.", false) {

            @Override
            public double getDoubleValue() {
                return applyCountAtLastExecution;
            }
        });
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getFullName() {
        if (this.fullName == null) {
            return getDefaultFullName();
        }
        return this.fullName;
    }

    public abstract String getDefaultFullName();

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty
    public abstract OperatorGroup getGroup();

    @JsonProperty
    public abstract String getDescription();

    @JsonProperty
    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    @JsonProperty
    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    @JsonProperty
    public Status getStatus() {
        if (status == null) {
            return Status.QUEUE;
        }
        return status;
    }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        } else {
            this.status = Status.QUEUE;
        }
    }

    /**
     * Adds an implementation of Value.
     */
    public void addValue(Value value) {
        valueMap.put(value.getKey(), value);
    }

    /**
     * Returns the value of the Value with the given key.
     */
    public final Value getValue(String key) {
        return valueMap.get(key);
    }

    /**
     * Returns all Values sorted by key.
     */
    public Collection<Value> getValues() {
        return valueMap.values();
    }

    /**
     * Returns the number of times this operator was already applied.
     */
    public int getApplyCount() {
        return applyCountAtLastExecution;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     */
//    public void setCompatibilityLevel(OperatorVersion compatibilityLevel) {
//        this.compatibilityLevel = compatibilityLevel;
//    }

//    public OperatorVersion getCompatibilityLevel() {
//        if (compatibilityLevel == null) {
//            compatibilityLevel = OperatorVersion.getLatestVersion();
//        }
//        return compatibilityLevel;
//    }
    @JsonProperty
    public InputPorts getInputPorts() {
        return inputPorts;
    }

    @JsonProperty
    public OutputPorts getOutputPorts() {
        return outputPorts;
    }

    /**
     * This method returns an {@link InputPorts} object for port initialization. Useful for adding
     * an arbitrary implementation (e.g. changing port creation & (dis)connection behavior,
     * optionally by customized {@link InputPort} instances) by overriding this method.
     *
     * @param portOwner The owner of the ports.
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
     * @param portOwner The owner of the ports.
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

    /**
     * Deletes this operator removing it from the name map of the process.
     */
    protected void unregisterOperator(OperatorProcess process) {
        process.unregisterName(name);
    }

    public void removeAndKeepConnections(List<Operator> keepConnectionsTo) {

        getInputPorts().disconnectAllBut(keepConnectionsTo);
        getOutputPorts().disconnectAllBut(keepConnectionsTo);

        OperatorProcess process = getProcess();
        if (enclosingExecutionUnit != null) {
            enclosingExecutionUnit.removeOperator(this);
        }
        if (process != null) {
            unregisterOperator(process);
        }
    }

    /**
     * Removes this operator from its parent.
     */
    public void remove() {
        removeAndKeepConnections(null);
    }

    /**
     * Performs the actual work of the operator and must be implemented by subclasses. Replaces the
     * old method <code>apply()</code>.
     */
    public void doWork() throws OperatorException {
    }

    public void execute() {
        try {
            setStatus(Status.RUNNING);
            isRunning = true;
            applyCountAtLastExecution = applyCount.incrementAndGet();
            doWork();
            setStatus(Status.SUCCESS);
        } catch (OperatorException oe) {
            setStatus(Status.FAILED);
            throw oe;
        } catch (Exception oe) {
            setStatus(Status.FAILED);
            throw oe;
        } finally {
            isRunning = false;
        }
    }

    /**
     * Clears all errors, checks the operator and its children and propagates meta data, propgatates
     * dirtyness and sorts execution order.
     */
    public void checkAll() {
        checkOperator();
        getRoot().transformMetaData();
        updateExecutionOrder();
    }

    private final void checkOperator() {
        checkProperties();
    }

    public void updateExecutionOrder() {
    }

    /**
     * Will count an error if a non optional property has no default value and is not defined by
     * user. Returns the total number of errors.
     */
    public int checkProperties() {
        int errorCount = 0;
        Iterator<ParameterType> i = getParameters().getParameterTypes().iterator();
        while (i.hasNext()) {
            ParameterType type = i.next();
            boolean optional = type.isOptional();
            if (!optional) {
                boolean parameterSet = getParameters().isSet(type.getKey());
                if (type.getDefaultValue() == null && !parameterSet) {
                    addError(new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, portOwner,
                            "undefined_parameter", new Object[]{type.getKey().replace('_', ' ')}));
                    errorCount++;
                } else if (type instanceof ParameterTypeAttribute && parameterSet) {
                    try {
                        if ("".equals(getParameter(type.getKey()))) {
                            addError(new SimpleProcessSetupError(ProcessSetupError.Severity.ERROR, portOwner,
                                    "undefined_parameter", new Object[]{type.getKey().replace('_', ' ')}));
                            errorCount++;
                        }
                    } catch (UndefinedParameterError e) {
                        // Ignore
                    }
                }
            }
            if (!optional && type instanceof ParameterTypeDate) {
                String value = getParameters().getParameter(type.getKey());
                if (value != null && !ParameterTypeDate.isValidDate(value)) {
                    addError(new SimpleProcessSetupError(ProcessSetupError.Severity.WARNING, portOwner, "invalid_date_format",
                            new Object[]{type.getKey().replace('_', ' '), value}));
                }
            }
        }
        return errorCount;
    }

    /**
     * This method should be called within long running loops of an operator to check if the user
     * has canceled the execution in the mean while. This then will throw a
     * {@link ProcessStoppedException} to cancel the execution.
     */
    public final void checkForStop() throws ProcessStoppedException {
        // TODO
    }

    /**
     * Returns the first ancestor that does not have a parent. Note that this is not necessarily a
     * ProcessRootOperator!
     */
    public Operator getRoot() {
        if (getParent() == null) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }

    public Logger getLogger() {
        if (getProcess() == null) {
            return logger;
        } else {
            return getProcess().getLogger();
        }
    }

    /**
     * Returns the parameter type with the given name. Will return null if this operator does not
     * have a parameter with the given name.
     */
    public ParameterType getParameterType(String name) {
        Iterator<ParameterType> i = getParameters().getParameterTypes().iterator();
        while (i.hasNext()) {
            ParameterType current = i.next();
            if (current.getKey().equals(name)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Returns a list of <tt>ParameterTypes</tt> describing the parameters of this operator. The
     * default implementation returns an empty list if no input objects can be retained and special
     * parameters for those input objects which can be prevented from being consumed.
     * <p>
     * ATTENTION! This will create new parameterTypes. For calling already existing parameter types
     * use getParameters().getParameterTypes();
     */
    @JsonProperty
    @Override
    public List<ParameterType> getParameterTypes() {
        return new LinkedList<>();
    }

    /**
     * Returns a collection of all parameters of this operator. If the parameters object has not
     * been created yet, it will now be created. Creation had to be moved out of constructor for
     * meta data handling in subclasses needing a port.
     */
    @JsonProperty
    @Override
    public Parameters getParameters() {
        if (parameters == null) {
            // if not loaded already: do now
            parameters = new Parameters(getParameterTypes());
        }
        return parameters;
    }

    public boolean isParameterExist(String key) {
        return parameters != null && parameters.isSpecified(key);
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

    /**
     * Returns a single named parameter and casts it to File. This file is already resolved against
     * the process definition file but missing directories will not be created. If the parameter
     * name defines a non-optional parameter which is not set and has no default value, a
     * UndefinedParameterError will be thrown. If the parameter is optional and was not set this
     * method returns null. Operators should always use this method instead of directly using the
     * method {@link Process#resolveFileName(String)}.
     */
    @Override
    public java.io.File getParameterAsFile(String key) throws UserError {
        return getParameterAsFile(key, false);
    }

    /**
     * Returns a single named parameter and casts it to File. This file is already resolved against
     * the process definition file and missing directories will be created. If the parameter name
     * defines a non-optional parameter which is not set and has no default value, a
     * UndefinedParameterError will be thrown. If the parameter is optional and was not set this
     * method returns null. Operators should always use this method instead of directly using the
     * method {@link Process#resolveFileName(String)}.
     *
     * @throws DirectoryCreationError
     */
//    @Override
    public java.io.File getParameterAsFile(String key, boolean createMissingDirectories) throws UserError {
        String fileName = getParameter(key);
        if (fileName == null) {
            return null;
        }

        /*Process process = getProcess();
        if (process != null) {
            File result = process.resolveFileName(fileName);
            if (createMissingDirectories) {
                File parent = result.getParentFile();
                if (parent != null) {
                    if (!parent.exists()) {
                        boolean isDirectoryCreated = parent.mkdirs();
                        if (!isDirectoryCreated) {
                            throw new UserError(null, "io.dir_creation_fail", parent.getAbsolutePath());
                        }
                    }
                }
            }
            return result;
        } else {*/
            getLogger().fine(getName() + " is not attached to a process. Trying '" + fileName + "' as absolute filename.");
            File result = new File(fileName);
            if (createMissingDirectories) {
                if (result.isDirectory()) {
                    boolean isDirectoryCreated = result.mkdirs();
                    if (!isDirectoryCreated) {
                        throw new UserError(null, "io.dir_creation_fail", result.getAbsolutePath());
                    }
                } else {
                    File parent = result.getParentFile();
                    if (parent != null) {
                        if (!parent.exists()) {
                            boolean isDirectoryCreated = parent.mkdirs();
                            if (!isDirectoryCreated) {
                                throw new UserError(null, "io.dir_creation_fail", parent.getAbsolutePath());
                            }
                        }

                    }
                }
            }
            return result;
//        }
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
    public ExecutionUnit getExecutionUnit() {
        return enclosingExecutionUnit;
    }

    /**
     * If this method is called for perform the meta data transformation on this operator. It needs
     * the meta data on the input Ports to be already calculated.
     */
    public void transformMetaData() {
        getInputPorts().checkPreconditions();
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

    public UserData<Object> getUserData(String key) {
        synchronized (this.userDataLock) {
            if (this.userData == null) {
                return null;
            } else {
                return this.userData.get(key);
            }
        }
    }

    public void setUserData(String key, UserData<Object> data) {
        synchronized (this.userDataLock) {
            if (this.userData == null) {
                this.userData = new TreeMap<>();
            }
            this.userData.put(key, data);
        }
    }

    public IOContainer getResult(){
        return new IOContainer(new ArrayList<>());
    }
}
