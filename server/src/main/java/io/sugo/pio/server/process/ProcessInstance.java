package io.sugo.pio.server.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.Process;
import io.sugo.pio.metadata.MetadataProcessInstanceManager;
import io.sugo.pio.operator.Status;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by root on 16-12-29.
 */
public class ProcessInstance implements Serializable {
    private final String id;
    private final MetadataProcessInstanceManager metadataProcessInstanceManager;
    private Process process;
    private Status status = Status.QUEUE;
    private Status preStatus;

    public ProcessInstance(Process process, MetadataProcessInstanceManager metadataProcessInstanceManager) {
        this.process = process;
        this.id = process.getId();
        this.metadataProcessInstanceManager = metadataProcessInstanceManager;
    }

    public String getId() {
        return id;
    }

    @JsonProperty
    public String getProcessName() {
        return process.getName();
    }

    public Process getProcess() {
        return process;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.preStatus = this.status;
        this.status = status;
        metadataProcessInstanceManager.updateStatus(this);
    }

    public Status getPreStatus() {
        return preStatus;
    }

    public void run() {
        setStatus(Status.RUNNING);
        process.run();
    }

    public void success() {
        setStatus(Status.SUCCESS);
    }

    public void failed() {
        setStatus(Status.FAILED);
    }

}
