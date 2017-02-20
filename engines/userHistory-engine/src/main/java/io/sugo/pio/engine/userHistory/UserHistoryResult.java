package io.sugo.pio.engine.userHistory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 */
public class UserHistoryResult {
    private List<String> items;

    public UserHistoryResult(List<String> items){
        this.items = items;
    }

    @JsonProperty
    public List<String> getItems(){
        return items;
    }
}
