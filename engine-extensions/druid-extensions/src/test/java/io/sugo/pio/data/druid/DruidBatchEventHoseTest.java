package io.sugo.pio.data.druid;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 */
public class DruidBatchEventHoseTest {
    @Test
    public void testFormat() {
        BatchEventHose hose = new DruidEventHose("192.168.0.219:8081", "wuxianjiRT");
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("test");
        sparkConf.setMaster("local");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        hose.find(sparkContext, DateTime.parse("2016-04-01").getMillis(), DateTime.parse("2016-07-10").getMillis()).foreach(new PrintlnFunc());
    }

    static class PrintlnFunc implements VoidFunction<Event> {
        @Override
        public void call(Event event) throws Exception {
            System.out.println(event);
        }
    }
}
