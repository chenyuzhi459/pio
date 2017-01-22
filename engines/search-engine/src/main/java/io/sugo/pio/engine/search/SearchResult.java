package io.sugo.pio.engine.search;

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

    public List<String> getItems(){
        return items;
    }
    public List<String> getNames(){
        return names;
    }
}
