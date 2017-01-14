package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface DataSource<TD> {
    TD readTraining(JavaSparkContext sc);
}
