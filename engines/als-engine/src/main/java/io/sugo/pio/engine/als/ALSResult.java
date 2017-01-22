package io.sugo.pio.engine.als;

import java.util.List;
import java.util.Map;

/**
 */
public class ALSResult {
    private List<String> items;

    public ALSResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}
