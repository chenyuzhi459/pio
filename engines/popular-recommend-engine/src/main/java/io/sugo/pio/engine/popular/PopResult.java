package io.sugo.pio.engine.popular;

import java.util.List;

/**
 */
public class PopResult {
    private List<String> items;

    public PopResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}
