package io.sugo.pio.engine.popular;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class PopQuery implements PredictionQueryObject {
    private String category;
    private String num;


    @JsonCreator
    public PopQuery(
            @JsonProperty("category") String category,
            @JsonProperty("num") String num
    ) {
        this.category = category;
        this.num = num;

    }

    public String getTags() {
        return category;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "itempop";
    }
}
