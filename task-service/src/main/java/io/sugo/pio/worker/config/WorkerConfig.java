package io.sugo.pio.worker.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.server.PioNode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 */
public class WorkerConfig {
    @JsonProperty
    @NotNull
    private String ip = PioNode.getDefaultHost();

    @JsonProperty
    @NotNull
    private String version = "0";

    @JsonProperty
    @Min(1)
    private int capacity = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    public String getIp()
    {
        return ip;
    }

    public String getVersion()
    {
        return version;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public WorkerConfig setCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }
}
