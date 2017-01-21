package io.sugo.pio.engine.search;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchQuery {
    private String item_name;
    private String num;


    @JsonCreator
    public SearchQuery(
            @JsonProperty("item_name") String item_name,
            @JsonProperty("num") String num
    ) {
        this.item_name = item_name;
        this.num = num;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getNum() {
        return num;
    }
}
