package io.sugo.pio.data.druid;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.data.druid.format.DruidInputFormat;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.util.Map;

/**
 */
public class DruidEventHose implements BatchEventHose {
    @JsonProperty("datasource")
    private String datasource;

    @JsonProperty("url")
    private String coordinatorUrl;

    public DruidEventHose(
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
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        Configuration conf = new Configuration();
        conf.set(DruidInputFormat.DRUID_COORDINATOR_URL, coordinatorUrl);
        conf.set(DruidInputFormat.DRUID_DATASOURCE, datasource);
        conf.set(DruidInputFormat.DRUID_STARTTIME, Long.toString(starttime));
        conf.set(DruidInputFormat.DRUID_ENDTIME, Long.toString(endTime));

        JavaPairRDD<Long, Map> rdd = sc.newAPIHadoopRDD(conf, DruidInputFormat.class, Long.class, Map.class);
        return rdd.map(new Function<Tuple2<Long,Map>, Event>() {
            @Override
            public Event call(Tuple2<Long, Map> v1) throws Exception {
                return new Event(v1._1(), v1._2());
            }
        });
    }
}
