package io.sugo.pio.engine.common.data;

import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by penghuan on 2017/4/28.
 */
public class Event {

    private long timestamp;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public Event() {}

    public Event(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void putProperties(String key, Object value) {
        properties.put(key, value);
    }

    public static String serialize(Event event) {
        return JSON.toJSONString(event);
    }

    public static Event deserialize(String eventStr) {
        return JSON.parseObject(eventStr, Event.class);
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", properties=" + properties +
                '}';
    }
}
