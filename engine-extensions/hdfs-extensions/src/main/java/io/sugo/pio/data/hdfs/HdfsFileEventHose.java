package io.sugo.pio.data.hdfs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.Event;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.Map;

/**
 */
public class HdfsFileEventHose implements BatchEventHose {
    private final String[] header;
    private final String seperator;
    private final String path;

    public HdfsFileEventHose(@JsonProperty("header") String[] header,
                             @JsonProperty("path") String path,
                             @JsonProperty("seperator") String seperator) {
        this.header = header;
        this.path = path;
        this.seperator = seperator;
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc) {
        return sc.textFile(path).map(new MapStringToEventFunc(seperator, header));
    }

    @Override
    public JavaRDD<Event> find(JavaSparkContext sc, long starttime, long endTime) {
        return find(sc);
    }

    private static class MapStringToEventFunc implements Function<String, Event> {
        private final String seperator;
        private final String[] header;

        MapStringToEventFunc(String seperator, String[] header) {
            this.seperator = seperator;
            this.header = header;
        }

        @Override
        public Event call(String str) throws Exception {
            String[] res = str.split(seperator);
            Map<String, Object> map = Maps.newHashMap();
            for(int i=0;i<header.length;i++) {
                map.put(header[0], res[0]);
            }
            return new Event(System.currentTimeMillis(), map);
        }
    }

    @JsonProperty
    public String getSeperator() {
        return seperator;
    }

    @JsonProperty
    public String getPath() {
        return path;
    }
}
