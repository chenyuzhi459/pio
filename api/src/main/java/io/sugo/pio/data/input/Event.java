package io.sugo.pio.data.input;

import org.joda.time.DateTime;

import java.util.Map;

/**
 */
public class Event {
    private DateTime timestamp;
    private Map<String, Object> properties;

    public Event(DateTime timestamp,
                 Map<String, Object> properties) {
        this.timestamp = timestamp;
        this.properties = properties;
    }

    public DateTime getTimestamp() {
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
