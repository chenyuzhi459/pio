package io.sugo.pio.engine.data.input;

import java.util.Map;

/**
 */
public class Event {
    private long timestamp;
    private Map<String, Object> properties;

    public Event(long timestamp,
                 Map<String, Object> properties) {
        this.timestamp = timestamp;
        this.properties = properties;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", properties=" + properties +
                '}';
    }
}
