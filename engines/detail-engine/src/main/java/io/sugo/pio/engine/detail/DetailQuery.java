package io.sugo.pio.engine.detail;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class DetailQuery implements PredictionQueryObject {
    private String itemId;
    private String num;

    @JsonCreator
    public DetailQuery(@JsonProperty("itemId") String itemId,
                       @JsonProperty("num") String num
    ) {
        this.itemId = itemId;
        this.num = num;
    }

    public String getItemId() {
        return itemId;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "detail";
    }
}
