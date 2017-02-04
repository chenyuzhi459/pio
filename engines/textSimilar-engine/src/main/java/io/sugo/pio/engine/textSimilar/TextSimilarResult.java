package io.sugo.pio.engine.textSimilar;

import java.util.List;

/**
 */
public class TextSimilarResult {
    private List<String> items;

    public TextSimilarResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}
