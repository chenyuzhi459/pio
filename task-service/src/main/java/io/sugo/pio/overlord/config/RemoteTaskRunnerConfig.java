package io.sugo.pio.overlord.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.curator.CuratorUtils;
import org.joda.time.Period;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 */
public class RemoteTaskRunnerConfig extends WorkerTaskRunnerConfig {
    @JsonProperty
    @NotNull
    private Period taskAssignmentTimeout = new Period("PT5M");

    @JsonProperty
    @NotNull
    private Period taskCleanupTimeout = new Period("PT15M");

    @JsonProperty
    private Period taskShutdownLinkTimeout = new Period("PT1M");

    @JsonProperty
    @Min(1)
    private int pendingTasksRunnerNumThreads = 1;

    @JsonProperty
    @Min(10 * 1024)
    private int maxZnodeBytes = CuratorUtils.DEFAULT_MAX_ZNODE_BYTES;

    public Period getTaskAssignmentTimeout()
    {
        return taskAssignmentTimeout;
    }

    @JsonProperty
    public Period getTaskCleanupTimeout(){
        return taskCleanupTimeout;
    }

    public Period getTaskShutdownLinkTimeout()
    {
        return taskShutdownLinkTimeout;
    }


    public int getPendingTasksRunnerNumThreads()
    {
        return pendingTasksRunnerNumThreads;
    }

    public int getMaxZnodeBytes()
    {
        return maxZnodeBytes;
    }
}
