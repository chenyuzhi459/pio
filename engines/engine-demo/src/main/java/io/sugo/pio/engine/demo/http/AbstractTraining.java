package io.sugo.pio.engine.demo.http;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

/**
 */
public abstract class AbstractTraining {
    private JavaSparkContext sc;

    public AbstractTraining() {
    }

    protected void init() {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("test");
        sc = new JavaSparkContext(sparkConf);
    }

    public void train() throws IOException {
        init();
        doTrain(sc);
        close();
    }

    protected abstract void doTrain(JavaSparkContext sc) throws IOException;

    protected void close() {
        sc.close();
    }
}
