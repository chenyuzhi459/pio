package sugo.io.pio.data.input;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventHoseType")
public interface BatchEventHose {
    RDD<Event> find(JavaSparkContext sc);
}
