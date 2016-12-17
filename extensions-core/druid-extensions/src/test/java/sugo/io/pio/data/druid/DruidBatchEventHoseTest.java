package sugo.io.pio.data.druid;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.joda.time.DateTime;
import org.junit.Test;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;

/**
 */
public class DruidBatchEventHoseTest {
//    @Test
//    public void testFormat() {
//        BatchEventHose hose = new DruidBatchEventHose("192.168.0.219:8081", "wuxianjiRT");
//        SparkConf sparkConf = new SparkConf();
//        sparkConf.setAppName("test");
//        sparkConf.setMaster("local");
//        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
//        hose.find(sparkContext, DateTime.parse("2016-04-01"), DateTime.parse("2016-04-10")).foreach(new PrintlnFunc());
//    }
//
//    static class PrintlnFunc implements VoidFunction<Event> {
//        @Override
//        public void call(Event event) throws Exception {
//            System.out.println(event);
//        }
//    }
}
