package io.sugo.pio.engine.demo.http;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

/**
 */
public abstract class AbstractTraining {
    public AbstractTraining() {
    }

    protected JavaSparkContext init() {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("test");
        return new JavaSparkContext(sparkConf);
    }

    public void train() throws IOException {
        JavaSparkContext sc = init();
        doTrain(sc);
        sc.close();
    }

    protected abstract void doTrain(JavaSparkContext sc) throws IOException;
}
