package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-1-5.
 */
public class CustomParameterDesc implements Description {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private List<Description> params;

    public CustomParameterDesc(String name, String description) {
        this.name = name;
        this.description = description;
        this.params = new ArrayList<>();
    }

    public CustomParameterDesc(String name, String description, List<Description> params) {
        this.name = name;
        this.description = description;
        this.params = params;
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

    public CustomParameterDesc addParameterDesc(Description desc) {
        params.add(desc);
        return this;
    }

    public static CustomParameterDesc create(String name, String description) {
        return new CustomParameterDesc(name, description);
    }

    public static CustomParameterDesc create(String name, String description, List<Description> params) {
        return new CustomParameterDesc(name, description, params);
    }
}
