package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CompareTuple<K, T> implements Serializable {
    @JsonProperty
    private K name;
    @JsonProperty
    private T type;
    @JsonProperty
    private String desc;

    public CompareTuple(K name, T type) {
        this.name = name;
        this.type = type;
        this.desc = name.toString();
    }

    public CompareTuple(K name, T type, String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public K getName() {
        return name;
    }

    public T getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}