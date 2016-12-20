package io.sugo.pio.runner.pushdown;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

/**
 */
public abstract class PushdownRunner
        implements Serializable {
    protected void run(String[] encodedArgs)
            throws SparkException, Throwable
    {
        JavaSparkContext sc = null;
        try {
            SparkConf sparkConf = new SparkConf();
            sc = new JavaSparkContext(sparkConf);

            runJob(sc, null);
        } catch (Throwable throwable) {

        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    protected abstract OutputParams runJob(JavaSparkContext paramJavaSparkContext, InputParams paramInputParams)
            throws SparkException;

    protected static class OutputParams
            implements Serializable
    {
        private static final long serialVersionUID = 1L;
    }

    protected static class InputParams
            implements Serializable
    {
        private static final long serialVersionUID = 1L;
    }
}