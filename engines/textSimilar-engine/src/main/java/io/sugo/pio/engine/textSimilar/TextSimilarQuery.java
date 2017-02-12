package io.sugo.pio.engine.textSimilar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

/**
 */
public class TextSimilarQuery implements PredictionQueryObject {
    private String item_id;
    private String num;

    @JsonCreator
    public TextSimilarQuery(
            @JsonProperty("item_id") String item_id,
            @JsonProperty("num") String num
    ) {
        this.item_id = item_id;
        this.num = num;
    }

    public String getItem_id() {
        return item_id;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "itemTextSimilar";
    }
}