package io.sugo.pio.engine.training;

import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple3;
import java.util.List;

/**
 */
public interface DataSource<TD, EQ, EAR> {
    TD readTraining(JavaSparkContext sc);

    List<Tuple3<TD, EQ, EAR>> readEval(JavaSparkContext sc);
}
