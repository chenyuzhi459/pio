package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 */
public class ALSResult {
    private List<String> items;

    public ALSResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}
