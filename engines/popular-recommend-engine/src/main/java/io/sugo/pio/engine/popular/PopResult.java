package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class PopResult {
    private List<String> items;

    public PopResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}
