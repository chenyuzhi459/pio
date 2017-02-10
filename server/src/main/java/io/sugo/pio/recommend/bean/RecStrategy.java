package io.sugo.pio.recommend.bean;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.List;

public class RecStrategy implements Serializable {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<String> types;
    @JsonProperty
    private String orderField;
    @JsonProperty
    private Boolean asc = false;
    @JsonProperty
    private float percent;

    private RecInstance recInstance;

    @JsonCreator
    public RecStrategy(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("types") List<String> types,
            @JsonProperty("orderField") String orderField,
            @JsonProperty("asc") Boolean asc,
            @JsonProperty("percent") Float percent
    ) {
        Preconditions.checkNotNull(name, "Must specify name");
        Preconditions.checkArgument(types != null && types.isEmpty(), "Must specify types");
        Preconditions.checkNotNull(orderField, "Must specify orderField");
        Preconditions.checkNotNull(percent, "Must specify orderField");
        this.id = id;
        this.name = name;
        this.types = types;
        this.orderField = orderField;
        this.asc = asc == null ? false : asc;
        this.percent = percent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public RecInstance getRecInstance() {
        return recInstance;
    }

    public void setRecInstance(RecInstance recInstance) {
        this.recInstance = recInstance;
    }
}
