package io.sugo.pio.jdbc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class JdbcBatchEventHose implements BatchEventHose {
    private String timeColumn;

    @JsonCreator
    public JdbcBatchEventHose(@JsonProperty("timeColumn") String timeColumn) {
        this.timeColumn = timeColumn;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return null;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        if (null == timeColumn) {
            return find(sc);
        }

        return null;
    }
}
