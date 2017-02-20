package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class DetailResult {
    private List<String> items;

    public DetailResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}

