package io.sugo.pio.engine.demo.data;

import com.google.common.collect.Maps;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import io.sugo.pio.engine.demo.Constants;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.Map;

/**
 */
public class MovieBatchEventHose implements BatchEventHose {
    private final String filepath;
    private final String seperator;

    public MovieBatchEventHose(String filepath, String seperator) {
        this.filepath = filepath;
        this.seperator = seperator;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return sc.textFile(filepath).map(new MapStringToEventFunc(seperator));
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
            map.put(Constants.USER_ID, Integer.parseInt(res[0]));
            map.put(Constants.ITEM_ID, Integer.parseInt(res[1]));
            map.put(Constants.GRADE, Float.parseFloat(res[2]));
            map.put(Constants.TIMENUM, Long.parseLong(res[3]));
            return new Event(System.currentTimeMillis(), map);
        }
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        return find(sc);
    }
}

