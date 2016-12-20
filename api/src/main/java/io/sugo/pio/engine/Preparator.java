package io.sugo.pio.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Preparator<TD, PD> {
    PD prepare(JavaSparkContext sc, TD td);
}
