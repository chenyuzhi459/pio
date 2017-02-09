package io.sugo.pio.engine.data.output;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.OutputStream;
import java.io.Serializable;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "local", value = LocalFileRepository.class)
})
public interface Repository extends Serializable {
    void init();

    OutputStream openOutput(String name);

    String[] listAll();

    long getSize(String name);

    void rename(String source, String dest);

    void create(String name);

    boolean delete(String name);

    boolean exists(String name);

    FSInputStream openInput(String name);

    boolean isDistributed();
}
