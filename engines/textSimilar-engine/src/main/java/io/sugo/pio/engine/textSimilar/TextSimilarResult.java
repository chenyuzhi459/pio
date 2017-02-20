package io.sugo.pio.engine.textSimilar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class TextSimilarResult {
    private List<String> items;

    public TextSimilarResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}
