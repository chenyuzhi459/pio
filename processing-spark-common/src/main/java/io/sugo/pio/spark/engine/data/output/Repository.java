package io.sugo.pio.spark.engine.data.output;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.OutputStream;
import java.io.Serializable;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "repositoryType")
public interface Repository extends Serializable {
    OutputStream openOutput(String name);

    String[] listAll();

    long getSize(String name);

    void rename(String source, String dest);

    void delete(String name);

    FSInputStream openInput(String name);
}
