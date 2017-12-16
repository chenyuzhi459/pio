package io.sugo.pio.engine.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 */
public class ObjectMapperUtil {
    private static ObjectMapper jsonMapper;

    static {
        jsonMapper = new ObjectMapper();
    }

    public static ObjectMapper getObjectMapper() {
        return jsonMapper;
    }

}
