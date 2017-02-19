package io.sugo.pio.client.broker;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class BrokerServiceSelectorConfig {
    public static final String DEFAULT_SERVICE_NAME = "pio/broker";

    @JsonProperty
    private String serviceName = DEFAULT_SERVICE_NAME;

    public String getServiceName()
    {
        return serviceName;
    }
}
