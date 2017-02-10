package io.sugo.pio.engine.data.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.map.HashedMap;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class MappingEventHose implements BatchEventHose {
    private BatchEventHose delegate;
    private Map<String, String> mapping;

    @JsonCreator
    public MappingEventHose(@JsonProperty("delegate") BatchEventHose delegate,
                            @JsonProperty("mapping") Map<String, String> mapping) {
        this.delegate = delegate;
        this.mapping = mapping;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return delegate.find(sc).map(new FieldNameMapping(mapping));
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        return delegate.find(sc, starttime, endTime).map(new FieldNameMapping(mapping));
    }

    static class FieldNameMapping implements Function<Event, Event> {
        private Map<String, String> mapping;

        FieldNameMapping(Map<String, String> mapping) {
            this.mapping = mapping;
        }

        @Override
        public Event call(Event event) throws Exception {
            Map<String, Object> newProperties = new HashMap<String, Object>();
            Map<String, Object> oldProperties = event.getProperties();
            for(Map.Entry<String, Object> entry: oldProperties.entrySet()) {
                if (mapping.containsKey(entry.getKey())) {
                    newProperties.put(mapping.get(entry.getKey()), entry.getValue());
                } else {
                    newProperties.put(entry.getKey(), entry.getValue());
                }
            }

            return new Event(event.getTimestamp(), newProperties);
        }
    }
}
