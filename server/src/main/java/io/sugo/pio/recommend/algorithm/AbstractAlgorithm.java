package io.sugo.pio.recommend.algorithm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAlgorithm implements Serializable {
    private static String name;
    private String description;
    private Map<String, String> args = new HashMap<>();

    protected static AbstractAlgorithm algorithm;

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty
    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }

    protected void addArg(String key, String value) {
        this.args.put(key, value);
    }
}
