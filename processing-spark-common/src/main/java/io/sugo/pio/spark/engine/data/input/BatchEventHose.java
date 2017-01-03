package io.sugo.pio.spark.engine.data.input;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface BatchEventHose {
    JavaRDD<Event> find(JavaSparkContext sc);

    JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime);
}
