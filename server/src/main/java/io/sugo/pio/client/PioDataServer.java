package io.sugo.pio.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class PioDataServer {
    private final String type;
    private final String id;
    private final PioServer server;

    @JsonCreator
    public PioDataServer(@JsonProperty("type")String type, @JsonProperty("id")String id, @JsonProperty("server") PioServer server) {
        this.type = type;
        this.id = id;
        this.server = server;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public PioServer getServer() {
        return server;
    }
}
