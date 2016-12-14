package sugo.io.pio.data.input;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTime;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventHoseType")
public interface BatchEventHose {
    JavaRDD<Event> find(JavaSparkContext sc);

    JavaRDD<Event> find(JavaSparkContext sc, DateTime starttime, DateTime endTime);
}
