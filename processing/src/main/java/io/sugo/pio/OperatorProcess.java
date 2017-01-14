package io.sugo.pio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class OperatorProcess {

    private String id;
    private String name;
    private Status status = Status.QUEUE;
    private DateTime createTime;
    private DateTime updateTime;
    private ProcessRootOperator rootOperator;

    /**
     * This map holds the names of all operators in the process. Operators are automatically
     * registered during adding and unregistered after removal.
     */
    private Map<String, Operator> operatorNameMap = new HashMap<>();

    @JsonCreator
    public OperatorProcess(
            @JsonProperty("name") String name,
            @JsonProperty("rootOperator") ProcessRootOperator rootOperator
    ) {
        this.name = name;
        this.id = String.format("%s-%d", name, new DateTime().getMillis());
        setRootOperator(rootOperator);
        this.createTime = new DateTime();
        this.updateTime = this.createTime;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updateTime = new DateTime();
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

    public void setRootOperator(ProcessRootOperator rootOperator) {
        this.rootOperator = rootOperator;
    }

    public final IOContainer run() {
        setStatus(Status.RUNNING);
        rootOperator.execute();
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

    /** This method is used for unregistering a name from the operator name map. */
    public void unregisterName(final String name) {
        operatorNameMap.remove(name);
    }

    public void success() {
        setStatus(Status.SUCCESS);
    }

    public void failed() {
        setStatus(Status.FAILED);
    }
}
