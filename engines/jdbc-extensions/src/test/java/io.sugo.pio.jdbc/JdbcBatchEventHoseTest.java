package io.sugo.pio.jdbc;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.junit.Test;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class JdbcBatchEventHoseTest {
    @Test
    public void testReading() {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("wordcount");;
        JavaSparkContext sc = new JavaSparkContext(conf);
        String url = "jdbc:postgresql://192.168.0.210:5432/htc";
        String table = "item_data";
        String username = "postgres";
        String password = "123456";
        int count = 10;
        int par = 2;
        Set<String> pNames = new HashSet<String>();
        pNames.add("itemname");

        JdbcBatchEventHose jdbcBatchEventHose = new JdbcBatchEventHose(null,url,table,username,password,count,par,pNames);
        jdbcBatchEventHose.find(sc).foreach(new PrintlnFunction());
    }

    static class PrintlnFunction implements VoidFunction, Serializable {
        @Override
        public void call(Object o) throws Exception {
            System.out.println(o);
        }
    }
}
