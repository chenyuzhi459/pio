package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CompareTuple<K, T> implements Serializable {
    @JsonProperty
    private K name;
    @JsonProperty
    private T type;

    public CompareTuple(K name, T type) {
        this.name = name;
        this.type = type;
    }

    public K getName() {
        return name;
    }

    public T getType() {
        return type;
    }
}