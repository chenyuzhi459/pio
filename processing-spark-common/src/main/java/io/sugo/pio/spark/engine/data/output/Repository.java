package io.sugo.pio.spark.engine.data.output;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.InputStream;
import java.io.OutputStream;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "repositoryType")
public interface Repository {
    OutputStream openOutput();

    InputStream openInput();
}
