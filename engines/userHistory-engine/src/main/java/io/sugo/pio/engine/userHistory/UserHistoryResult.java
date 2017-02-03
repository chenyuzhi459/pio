package io.sugo.pio.engine.userHistory;

import java.util.List;

/**
 */
public class UserHistoryResult {
    private List<String> items;

    public UserHistoryResult(List<String> items){
        this.items = items;
    }

    public List<String> getItems(){
        return items;
    }
}
