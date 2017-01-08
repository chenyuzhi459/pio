package io.sugo.pio.spark.runner;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.JavaSparkContext;

/**
 */
public class SparkTestCountJobRunner {
    public static void main(String[] encodedArgs)
            throws SparkException
    {
        SparkConf conf = new SparkConf();
        JavaSparkContext sc = new JavaSparkContext(conf);
        System.out.println("ok");
    }
}
