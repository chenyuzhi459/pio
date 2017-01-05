package io.sugo.pio.engine;

import io.sugo.pio.data.input.BatchEventHose;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface DataSource<TD> {
    TD readTraining(JavaSparkContext sc, BatchEventHose eventHose);
}
