package io.sugo.pio.runner;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

/**
 */
public abstract class AbstractSparkRunner {
    static SparkConf conf;
    static JavaSparkContext sc;
    static SQLContext sqlContext;

    protected static void close() {
        sc.stop();
        sc.close();
    }

    protected static String[] init(String[] encodedArgs) {
        conf = new SparkConf();
        sc = new JavaSparkContext(conf);
        sqlContext = new SQLContext(sc);

        return encodedArgs;
    }

}
