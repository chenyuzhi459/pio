//package io.sugo.pio.server.process;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import io.sugo.pio.Process;
//import io.sugo.pio.metadata.MetadataProcessInstanceManager;
//import io.sugo.pio.operator.Status;
//import org.joda.time.DateTime;
//
//import java.io.Serializable;
//import java.util.UUID;
//
///**
// * Created by root on 16-12-29.
// */
//public class ProcessInstance implements Serializable {
//    private final String id;
//    private MetadataProcessInstanceManager metadataProcessInstanceManager;
//    private Process process;
//    private Status status = Status.QUEUE;
//    private Status preStatus;
//    private DateTime createTime;
//    private DateTime updateTime;
//
//    public ProcessInstance(Process process, Status status, DateTime createTime, DateTime updateTime) {
//        this.process = process;
//        this.id = process.getId();
//        this.status = status;
//        this.createTime = createTime;
//        this.updateTime = updateTime;
//    }
//
//    public ProcessInstance(Process process, MetadataProcessInstanceManager metadataProcessInstanceManager) {
//        this.process = process;
//        this.id = process.getId();
//        this.metadataProcessInstanceManager = metadataProcessInstanceManager;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getProcessName() {
//        return process.getName();
//    }
//
//    @JsonProperty
//    public Process getProcess() {
//        return process;
//    }
//
//    @JsonProperty
//    public Status getStatus() {
//        return status;
//    }
//
//    @JsonProperty
//    public DateTime getCreateTime() {
//        return createTime;
//    }
//
//    @JsonProperty
//    public DateTime getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setStatus(Status status) {
//        this.preStatus = this.status;
//        this.status = status;
//        metadataProcessInstanceManager.updateStatus(this);
//    }
//
//    public Status getPreStatus() {
//        return preStatus;
//    }
//
//    public void run() {
//        setStatus(Status.RUNNING);
//        process.run();
//    }
//
//    public void success() {
//        setStatus(Status.SUCCESS);
//    }
//
//    public void failed() {
//        setStatus(Status.FAILED);
//    }
//
//}
