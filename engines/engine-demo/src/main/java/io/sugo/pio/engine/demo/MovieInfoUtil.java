package io.sugo.pio.engine.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class MovieInfoUtil {
    private static Map<String, Map<String, Object>> movId2Info = new HashMap<>();

    static {
        InputStream inputStream = MovieInfoUtil.class.getClassLoader().getResourceAsStream("movieInf.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ObjectMapper jsonMapper = new ObjectMapper();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                Map<String, Object> movieInfo = jsonMapper.readValue(line, new HashMap<String, Object>().getClass());
                movId2Info.put((String)movieInfo.get("movid"), movieInfo);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> getMovieInfoByMovId(String movId) {
        return movId2Info.get(movId);
    }
}
