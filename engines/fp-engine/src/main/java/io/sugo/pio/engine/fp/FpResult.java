package io.sugo.pio.engine.fp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 */
public class FpResult {
    private List<String> items;

    public FpResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}
