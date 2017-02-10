package io.sugo.pio.engine.training;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class SparkContextSettings {
    private final String checkPointDir;

    @JsonCreator
    public SparkContextSettings(@JsonProperty("checkPointDir") String checkPointDir) {
        this.checkPointDir = checkPointDir;
    }

    @JsonProperty
    public String getCheckPointDir() {
        return checkPointDir;
    }
}
