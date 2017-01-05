package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by root on 17-1-5.
 */
public class ListParameterDesc implements Description {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private Description parameterDesc;

    public ListParameterDesc(String name, String description, Description parameterDesc) {
        this.name = name;
        this.description = description;
        this.parameterDesc = parameterDesc;
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

    public Description getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(Description parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public static ListParameterDesc create(String name, String description, Description parameterDesc) {
        return new ListParameterDesc(name, description, parameterDesc);
    }

    public static ListParameterDesc createInt(String name, String description) {
        return new ListParameterDesc(name, description, ParameterDesc.createInt("", ""));
    }

    public static ListParameterDesc createString(String name, String description) {
        return new ListParameterDesc(name, description, ParameterDesc.createString("", ""));
    }

    public static ListParameterDesc createDouble(String name, String description) {
        return new ListParameterDesc(name, description, ParameterDesc.createDouble("", ""));
    }
}
