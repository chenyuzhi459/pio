package io.sugo.pio.server.http.dto;

import java.io.Serializable;

/**
 */
public class OperatorParamDto implements Serializable{
    /**
     * The operator parameter key
     */
    private String key;

    /**
     * The operator parameter value
     */
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }
}
