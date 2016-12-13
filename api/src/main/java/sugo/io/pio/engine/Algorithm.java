package sugo.io.pio.engine;

import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public interface Algorithm<MD> {
    MD train(JavaSparkContext sc);
}
