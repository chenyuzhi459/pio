package sugo.io.pio.data.druid;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.joda.time.DateTime;
import scala.Tuple2;
import sugo.io.pio.data.druid.format.DruidInputFormat;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;

import java.io.Serializable;
import java.util.Map;

/**
 */
public class DruidBatchEventHose implements BatchEventHose, Serializable {
    @JsonProperty("datasource")
    private String datasource;

    @JsonProperty("url")
    private String coordinatorUrl;

    public DruidBatchEventHose(
        @JsonProperty("url") String coordinatorUrl,
        @JsonProperty("datasource") String datasource) {
        this.coordinatorUrl = coordinatorUrl;
        this.datasource = datasource;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, DateTime starttime, DateTime endTime) {
        Configuration conf = new Configuration();
        conf.set(DruidInputFormat.DRUID_COORDINATOR_URL, coordinatorUrl);
        conf.set(DruidInputFormat.DRUID_DATASOURCE, datasource);
        conf.set(DruidInputFormat.DRUID_STARTTIME, starttime.toString());
        conf.set(DruidInputFormat.DRUID_ENDTIME, endTime.toString());

        JavaPairRDD<DateTime, Map> rdd = sc.newAPIHadoopRDD(conf, DruidInputFormat.class, DateTime.class, Map.class);
        return rdd.map(new Function<Tuple2<DateTime,Map>, Event>() {
            @Override
            public Event call(Tuple2<DateTime, Map> v1) throws Exception {
                return new Event(v1._1(), v1._2());
            }
        });
    }
}
