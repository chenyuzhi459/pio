package io.sugo.pio.spark.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface DataSource<TD> {
    TD readTraining(JavaSparkContext sc);
}
