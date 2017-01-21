package io.sugo.pio.engine.popular;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class PopQuery implements PredictionQueryObject {
    private String detailCategory;
    private String num;


    @JsonCreator
    public PopQuery(
            @JsonProperty("detailCategory") String detailCategory,
            @JsonProperty("num") String num
    ) {
        this.detailCategory = detailCategory;
        this.num = num;

    }

    public String getDetailCategory() {
        return detailCategory;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "pop";
    }
}
