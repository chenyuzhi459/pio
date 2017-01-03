package io.sugo.pio.spark.transfer.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 */
public abstract class SparkParameter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T extends SparkParameter> T fromJson(String jsonString, Class<T> toClass) throws IOException {
        T t = objectMapper.readValue(jsonString, toClass);
        return t;
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
