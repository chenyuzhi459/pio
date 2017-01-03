package io.sugo.pio.spark.engine;

import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface DataSource<TD> {
    TD readTraining(JavaSparkContext sc, BatchEventHose eventHose);
}
