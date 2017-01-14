package io.sugo.pio.engine.data.input;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Map;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface PropertyHose {
    JavaRDD<Map<String, Object>> find(JavaSparkContext sc);
}
