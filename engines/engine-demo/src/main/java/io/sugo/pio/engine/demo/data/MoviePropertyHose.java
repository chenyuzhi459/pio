package io.sugo.pio.engine.demo.data;

import com.google.common.collect.Maps;
import io.sugo.pio.engine.data.input.PropertyHose;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class MoviePropertyHose implements PropertyHose {
    private final String filepath;
    private final String seperator;
    private final String[] itemGens;

    public MoviePropertyHose(String filepath, String seperator, String itemGens) {
        this.filepath = filepath;
        this.seperator = seperator;
        this.itemGens = itemGens.split(seperator);
    }

    @Override
    public JavaRDD<Map<String, Object>> find(JavaSparkContext sc) {
        return sc.textFile(filepath).map(new MapStringToPropFunc(seperator, itemGens));
    }

    private static class MapStringToPropFunc implements Function<String, Map<String, Object>> {
        private final String seperator;
        private final String[] itemGens;

        MapStringToPropFunc(String seperator, String[] itemGens) {
            this.seperator = seperator;
            this.itemGens = itemGens;
        }

        @Override
        public Map<String, Object> call(String line) throws Exception {
            String[] tokens = line.split(seperator);
            String title = tokens[1];
            if (title.contains(",")) {
                title = title.split(",")[0].trim();
            } else if (title.indexOf("(") > 0) {
                int index = title.indexOf("(");
                title = title.substring(0, index).trim();
            }

            List<String> gens = new ArrayList<>();
            for (int i=5;i<tokens.length;i++) {
                String flag = tokens[i];
                if ("1".equals(flag)) {
                    String gen = itemGens[i - 5];
                    gens.add(gen);
                }
            }

            Map<String, Object> map = Maps.newHashMap();
            map.put("movieId", Integer.parseInt(tokens[0]));
            map.put("title", title);
            map.put("tags", gens);
            return map;
        }
    }

}