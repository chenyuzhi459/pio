package io.sugo.pio.spark.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Preparator<TD, PD> {
    PD prepare(JavaSparkContext sc, TD td);
}