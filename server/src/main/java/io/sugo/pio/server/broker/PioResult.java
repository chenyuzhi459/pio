package io.sugo.pio.server.broker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PioResult implements Serializable {
    private List<String> items;

    @JsonCreator
    public PioResult(
            @JsonProperty("items") List<String> items
    ) {
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems() {
        return items;
    }
}

