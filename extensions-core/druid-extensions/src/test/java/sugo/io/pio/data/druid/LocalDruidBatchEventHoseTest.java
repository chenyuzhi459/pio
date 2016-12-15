package sugo.io.pio.data.druid;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.joda.time.DateTime;
import org.junit.Test;
import sugo.io.pio.data.druid.format.LocalDruidBatchEventHose;
import sugo.io.pio.data.input.BatchEventHose;

/**
 */
public class LocalDruidBatchEventHoseTest {
    @Test
    public void testFormat() {
        BatchEventHose hose = new LocalDruidBatchEventHose("/home/yaotc/Desktop", "wuxianji_rt");
        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("test");
        sparkConf.setMaster("local");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        long c = hose.find(sparkContext, DateTime.parse("2016-01-01"), DateTime.parse("2016-12-10")).count();
        System.out.println(c);
    }

}
