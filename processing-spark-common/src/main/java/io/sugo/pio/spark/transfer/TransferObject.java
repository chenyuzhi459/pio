package io.sugo.pio.spark.transfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 */
public class TransferObject {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T extends io.sugo.pio.spark.transfer.parameter.SparkParameter> T fromJson(String jsonString, Class<T> toClass) throws IOException {
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
