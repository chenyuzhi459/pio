package io.sugo.pio.engine.template.data.input;

import com.google.common.collect.Maps;
import io.sugo.pio.spark.engine.data.input.BatchEventHose;
import io.sugo.pio.spark.engine.data.input.Event;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

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
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
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
            map.put("userId", Integer.parseInt(res[0]));
            map.put("movieId", Integer.parseInt(res[1]));
            map.put("rating", Float.parseFloat(res[2]));

            return new Event(System.currentTimeMillis(), map);
        }
    }
}
