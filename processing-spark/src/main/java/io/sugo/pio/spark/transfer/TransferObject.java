package io.sugo.pio.spark.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 */
public class TransferObject {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T extends TransferObject> T fromJson(String jsonString, Class<T> toClass) throws IOException {
        T t = objectMapper.readValue(jsonString, toClass);;
        return t;
    }
}
