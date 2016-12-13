package sugo.io.pio.druid;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;

/**
 */
public class DruidBatchEventHose implements BatchEventHose {
    private String host;
    private int port;

    public DruidBatchEventHose(
        String host,
        int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RDD<Event> find(JavaSparkContext sc) {
        return null;
    }
}
