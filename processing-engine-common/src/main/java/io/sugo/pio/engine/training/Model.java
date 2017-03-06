package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Model<MD> {
    void save(JavaSparkContext sc, MD md);
}
