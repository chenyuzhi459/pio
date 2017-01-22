package io.sugo.pio.engine.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class Click {
    private String userId;
    private String itemId;
    private long timestamp;

    @JsonCreator
    public Click(@JsonProperty("userId") String userId, @JsonProperty("itemId")String itemId, @JsonProperty("timestamp")long timestamp) {
        this.userId = userId;
        this.itemId = itemId;
    }

    @JsonProperty
    public String getUserId() {
        return userId;
    }

    @JsonProperty
    public String getItemId() {
        return itemId;
    }

    @JsonProperty
    public long getTimestamp() {
        return timestamp;
    }
}
