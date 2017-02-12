package io.sugo.pio.client.task;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class TaskServiceSelectorConfig {
    public static final String DEFAULT_SERVICE_NAME = "pio/overlord";

    @JsonProperty
    private String serviceName = DEFAULT_SERVICE_NAME;

    public String getServiceName()
    {
        return serviceName;
    }
}
