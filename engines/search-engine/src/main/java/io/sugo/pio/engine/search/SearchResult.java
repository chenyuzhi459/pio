package io.sugo.pio.engine.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class SearchResult {
    private List<String> items;
    private List<String> names;

    public SearchResult(List<String> items, List<String> names){
        this.items = items;
        this.names = names;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }

    @JsonProperty
    public List<String> getNames(){
        return names;
    }
}
