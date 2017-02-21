package io.sugo.pio.recommend.algorithm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "algorithmType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = ALSAlgorithm.TYPE, value = ALSAlgorithm.class),
        @JsonSubTypes.Type(name = DetailAlgorithm.TYPE, value = DetailAlgorithm.class),
        @JsonSubTypes.Type(name = FpAlgorithm.TYPE, value = FpAlgorithm.class),
        @JsonSubTypes.Type(name = PopAlgorithm.TYPE, value = PopAlgorithm.class),
        @JsonSubTypes.Type(name = SearchAlgorithm.TYPE, value = SearchAlgorithm.class),
        @JsonSubTypes.Type(name = UserHistoryAlgorithm.TYPE, value = UserHistoryAlgorithm.class),
})
public abstract class AbstractAlgorithm implements Serializable {
    private String type;
    private String queryType;
    private String description;
    private Map<String, String> args = new HashMap<>();

    @JsonProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
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
