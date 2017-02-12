package io.sugo.pio.engine.demo.http;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

/**
 */
public abstract class AbstractEval {
    public AbstractEval() {
    }

    protected JavaSparkContext init() {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("eval");
        return new JavaSparkContext(sparkConf);
    }

    public void eval() throws IOException {
        JavaSparkContext sc = init();
        doEval(sc);
        sc.close();
    }

    protected abstract void doEval(JavaSparkContext sc) throws IOException;
}
