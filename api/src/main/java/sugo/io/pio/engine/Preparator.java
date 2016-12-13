package sugo.io.pio.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Preparator<PD> {
    PD prepare(JavaSparkContext sc);
}
