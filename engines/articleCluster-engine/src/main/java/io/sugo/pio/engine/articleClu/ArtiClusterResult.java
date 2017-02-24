package io.sugo.pio.engine.articleClu;

import java.util.List;

/**
 */
public class ArtiClusterResult {
    private List<String> items;

    public ArtiClusterResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}
