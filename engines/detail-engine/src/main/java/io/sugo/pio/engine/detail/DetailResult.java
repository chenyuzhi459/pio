package io.sugo.pio.engine.detail;

import java.util.List;

/**
 */
public class DetailResult {
    private List<String> items;

    public DetailResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}

