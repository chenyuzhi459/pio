package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Algorithm<PD, MD> {
    MD train(JavaSparkContext sc, PD pd);
}
