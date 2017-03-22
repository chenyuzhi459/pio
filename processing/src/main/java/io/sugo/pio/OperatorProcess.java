package io.sugo.pio;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.IAE;
import com.metamx.common.logger.Logger;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.repository.RepositoryAccessor;
import io.sugo.pio.repository.RepositoryLocation;
import org.joda.time.DateTime;

import java.util.*;

/**
 */
public class OperatorProcess {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private Status status = Status.INIT;
    private DateTime createTime;
    private DateTime updateTime;
    private ProcessRootOperator rootOperator;
    private RepositoryAccessor repositoryAccessor;
    private ProcessLocation processLocation;
    private final List<ProcessStateListener> processStateListeners = Collections.synchronizedList(new LinkedList<>());
    /**
     * The macro handler can be used to replace (user defined) macro strings.
     */
    private final MacroHandler macroHandler = new MacroHandler(this);

    private static final Logger logger = new Logger(Process.class);

    /**
     * This map holds the names of all operators in the process. Operators are automatically
     * registered during adding and unregistered after removal.
     */
    private Map<String, Operator> operatorNameMap = new HashMap<>();

    private Set<Connection> connections;

    public OperatorProcess(String name) {
        this(name, new ProcessRootOperator());
    }

    public OperatorProcess(String name, ProcessRootOperator rootOperator) {
        this.name = name;
//        this.id = String.format("%s-%d", name, new DateTime().getMillis());
        this.id = UUID.randomUUID().toString();
        setRootOperator(rootOperator);
        this.createTime = new DateTime();
        this.updateTime = this.createTime;
        connections = new HashSet<>();
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getTenantId() {
        return tenantId;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    @JsonProperty
    public String getCreateTime() {
        return createTime.toString();
    }

    @JsonProperty
    public String getUpdateTime() {
        return updateTime.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updateTime = new DateTime();
    }

    public void clearStatus(){
        setStatus(Status.QUEUE);
        Collection<Operator> operators = operatorNameMap.values();
        for(Operator operator: operators){
            operator.setStatus(Status.QUEUE);
        }
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @JsonProperty("rootOperator")
    public ProcessRootOperator getRootOperator() {
        return rootOperator;
    }

    @JsonProperty
    public Set<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Set<Connection> connections) {
        this.connections.addAll(connections);
        if (connections != null && !connections.isEmpty()) {
            for (Connection connection : connections) {
                try {
                    connect(connection, false);
                } catch (IAE iae) {

                }
            }

            /*
             * Transform of metadata called when a single process first loaded.
             * Loaded here will cause perform problem.
             * @see SQLMetadataProcessManager#get(String id, boolean includeDelete)
             */
            /*try {
                rootOperator.getExecutionUnit().transformMetaData();
            } catch (Exception e) {

            }*/
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public RepositoryAccessor getRepositoryAccessor() {
        return repositoryAccessor;
    }

    public RepositoryLocation getRepositoryLocation() {
        if (processLocation instanceof RepositoryProcessLocation) {
            return ((RepositoryProcessLocation) processLocation).getRepositoryLocation();
        } else {
            return null;
        }
    }

    public void setProcessLocation(final ProcessLocation processLocation) {
        // keep process file version if same file, otherwise overwrite
        if (this.processLocation != null && !this.processLocation.equals(processLocation)) {
//            this.isProcessConverted = false;
//            getLogger().info("Decoupling process from location " + this.processLocation
//                    + ". Process is now associated with file " + processLocation + ".");
        }
        this.processLocation = processLocation;
//        fireUpdate();
    }

    public ProcessLocation getProcessLocation() {
        return this.processLocation;
    }

    public void addProcessStateListener(ProcessStateListener processStateListener) {
        this.processStateListeners.add(processStateListener);
    }

    /**
     * Removes the given process state listener.
     */
    public void removeProcessStateListener(ProcessStateListener processStateListener) {
        this.processStateListeners.remove(processStateListener);
    }

    /**
     * Returns the macro handler.
     */
    public MacroHandler getMacroHandler() {
        return this.macroHandler;
    }

    public void setRootOperator(ProcessRootOperator rootOperator) {
        this.rootOperator = rootOperator;
        this.rootOperator.setProcess(this);
    }

    public final IOContainer run() {
        setStatus(Status.RUNNING);
        rootOperator.processStarts();
        rootOperator.execute();
        rootOperator.processFinished();
        IOContainer result = rootOperator.getResults(false);
        return result;
    }

    /**
     * Returns a &quot;name (i)&quot; if name is already in use. This new name should then be used
     * as operator name.
     */
    public String registerName(final String name, final Operator operator) {
        if (operatorNameMap.get(name) != null) {
            String baseName = name;
            int index = baseName.indexOf(" (");
            if (index >= 0) {
                baseName = baseName.substring(0, index);
            }
            int i = 2;
            while (operatorNameMap.get(baseName + " (" + i + ")") != null) {
                i++;
            }
            String newName = baseName + " (" + i + ")";
            operatorNameMap.put(newName, operator);
            return newName;
        } else {
            operatorNameMap.put(name, operator);
            return name;
        }
    }

    /**
     * This method is used for unregistering a name from the operator name map.
     */
    public void unregisterName(final String name) {
        operatorNameMap.remove(name);
    }

    public void success() {
        setStatus(Status.SUCCESS);
    }

    public void failed() {
        setStatus(Status.FAILED);
    }

    public Operator getOperator(String operatorId) {
        Operator operator = operatorNameMap.get(operatorId);
        return operator;
    }

    public void removeOperator(String operatorId) {
        Operator operator = operatorNameMap.get(operatorId);
        if (operator != null) {
            operator.remove();
        }
        List<Connection> toDel = new ArrayList<>();
        for (Connection conn : connections) {
            if (conn.getFromOperator().equals(operatorId) || conn.getToOperator().equals(operatorId)) {
                toDel.add(conn);
            }
        }
        connections.removeAll(toDel);
    }

    public void connect(Connection dto, boolean add) {
        Operator fromOperator = operatorNameMap.get(dto.getFromOperator());
        Operator toOperator = operatorNameMap.get(dto.getToOperator());
        if (fromOperator == null || toOperator == null) {
            throw new IAE("fromOperator [%s] or toOperator [%s] not exists", dto.getFromOperator(), dto.getToOperator());
        }

        OutputPort fromPort = getFromPort(fromOperator, dto.getFromPort());
        InputPort toPort = getToPort(toOperator, dto.getToPort());
        if (fromPort == null || toPort == null) {
            throw new IAE("fromPort [%s] or toPort [%s] not exists", dto.getFromPort(), dto.getToPort());
        }

        fromPort.connectTo(toPort);
//        rootOperator.transformMetaData();
        if (add) {
            connections.add(dto);
        }
    }

    public void disconnect(Connection dto) {
        if (connections.contains(dto)) {
            Operator fromOperator = operatorNameMap.get(dto.getFromOperator());
            Operator toOperator = operatorNameMap.get(dto.getToOperator());
            if (fromOperator == null || toOperator == null) {
                throw new IAE("fromOperator [%s] or toOperator [%s] not exists", dto.getFromOperator(), dto.getToOperator());
            }

            OutputPort fromPort = getFromPort(fromOperator, dto.getFromPort());
            InputPort toPort = getToPort(toOperator, dto.getToPort());
            if (fromPort == null || toPort == null) {
                throw new IAE("fromPort [%s] or toPort [%s] not exists", dto.getFromPort(), dto.getToPort());
            }

            if (fromPort.isConnected()) {
                fromPort.disconnect();
            }
            rootOperator.transformMetaData();
            connections.remove(dto);
        }

    }

    private InputPort getToPort(Operator toOperator, String toPort) {
        List<InputPort> inputs = toOperator.getInputPorts().getAllPorts();
        for (InputPort input : inputs) {
            if (input.getName().equals(toPort)) {
                return input;
            }
        }

        // Continue search end ports, if the operator chain have
        List<InputPort> endPorts = getEndPorts(toOperator);
        for (InputPort input : endPorts) {
            if (input.getName().equals(toOperator)) {
                return input;
            }
        }

        return null;
    }

    private OutputPort getFromPort(Operator fromOperator, String fromPort) {
        List<OutputPort> outputs = fromOperator.getOutputPorts().getAllPorts();
        for (OutputPort output : outputs) {
            if (output.getName().equals(fromPort)) {
                return output;
            }
        }

        // Continue search start ports, if the operator chain have
        List<OutputPort> startPorts = getStartPorts(fromOperator);
        for (OutputPort output : startPorts) {
            if (output.getName().equals(fromPort)) {
                return output;
            }
        }

        return null;
    }

    /**
     * Return the inner source ports
     */
    private List<OutputPort> getStartPorts(Operator operator) {
        return operator.getExecutionUnit().getInnerSources().getAllPorts();
    }

    /**
     * Return the inner sink ports
     */
    private List<InputPort> getEndPorts(Operator operator) {
        return operator.getExecutionUnit().getInnerSinks().getAllPorts();
    }

}
