package sugo.io.pio.engine.template.data.input;

import com.google.common.collect.Maps;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.joda.time.DateTime;
import sugo.io.pio.data.input.BatchEventHose;
import sugo.io.pio.data.input.Event;

import java.util.Map;

/**
 */
public class MovielenBatchEventHose implements BatchEventHose {
    private final String filepath;
    private final String seperator;

    public MovielenBatchEventHose(String filepath, String seperator) {
        this.filepath = filepath;
        this.seperator = seperator;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return sc.textFile(filepath).map(new MapStringToEventFunc(seperator));
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, DateTime starttime, DateTime endTime) {
        return find(sc);
    }

    private static class MapStringToEventFunc implements Function<String, Event> {
        private final String seperator;

        MapStringToEventFunc(String seperator) {
            this.seperator = seperator;
        }

        @Override
        public Event call(String str) throws Exception {
            String[] res = str.split(seperator);
            Map<String, Object> map = Maps.newHashMap();
            map.put("userId", res[0]);
            map.put("movieId", res[1]);
            map.put("rating", res[2]);

            return new Event(DateTime.now(), map);
        }
    }
}
