package io.sugo.pio.engine.search;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class SearchQuery implements PredictionQueryObject {
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

    @Override
    public String getType() {
        return "itemSearch";
    }
}
