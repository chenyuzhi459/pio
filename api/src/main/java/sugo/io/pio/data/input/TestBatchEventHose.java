package sugo.io.pio.data.input;

import org.apache.spark.rdd.RDD;

/**
 */
public class TestBatchEventHose implements BatchEventHose {
    @Override
    public RDD<Event> find() {
        return null;
    }
}
