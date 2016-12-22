package io.sugo.pio.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Algorithm<PD, MD> {
    MD train(JavaSparkContext sc, PD pd);
}
