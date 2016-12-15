package sugo.io.pio.engine;

import org.apache.spark.api.java.JavaSparkContext;
import sugo.io.pio.data.input.BatchEventHose;

/**
 */
public interface DataSource<TD> {
    TD readTraining(JavaSparkContext sc, BatchEventHose eventHose);
}
