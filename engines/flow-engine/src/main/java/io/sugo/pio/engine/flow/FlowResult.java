package io.sugo.pio.engine.flow;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class FlowResult {
    private String totalPrice;
    private String groups;

    public FlowResult(String totalPrice, String groups){
        this.totalPrice = totalPrice;
        this.groups = groups;
    }

    @JsonProperty
    public String getTotalPrice(){
        return totalPrice;
    }

    @JsonProperty
    public String getGroups(){
        return groups;
    }
}
