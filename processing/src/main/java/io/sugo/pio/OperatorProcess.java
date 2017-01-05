package io.sugo.pio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import org.joda.time.DateTime;

/**
 */
public class OperatorProcess {

    private String id;
    private String name;
    private Status status = Status.QUEUE;
    private DateTime createTime;
    private DateTime updateTime;
    private ProcessRootOperator rootOperator;

    public OperatorProcess() {
    }

    @JsonCreator
    public OperatorProcess(String name, ProcessRootOperator rootOperator) {
        this.name = name;
        this.id = String.format("%s-%d", name, new DateTime().getMillis());
        setRootOperator(rootOperator);
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
    public DateTime getCreateTime() {
        return createTime;
    }

    @JsonProperty
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        IOContainer result = rootOperator.getResults();
        return result;
    }

    public void success() {
        setStatus(Status.SUCCESS);
    }

    public void failed() {
        setStatus(Status.FAILED);
    }
}
