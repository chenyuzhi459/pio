package io.sugo.pio.engine.data.input;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "mapping", value = MappingEventHose.class),
})
public interface BatchEventHose extends Serializable {
    JavaRDD<Event> find(JavaSparkContext sc);

    JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime);
}

