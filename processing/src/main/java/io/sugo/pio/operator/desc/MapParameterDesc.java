package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by root on 17-1-5.
 */
public class MapParameterDesc implements Description {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private ParamType keyType;
    @JsonProperty
    private ParamType valueType;

    public MapParameterDesc(String name, String description, ParamType keyType, ParamType valueType) {
        this.name = name;
        this.description = description;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParamType getKeyType() {
        return keyType;
    }

    public void setKeyType(ParamType keyType) {
        this.keyType = keyType;
    }

    public static MapParameterDesc create(String name, String description, ParamType keyType, ParamType valueType) {
        return new MapParameterDesc(name, description, keyType, valueType);
    }
}
