package io.sugo.pio.engine.als;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class ALSQuery implements PredictionQueryObject {
    private String userId;
    private String num;

    @JsonCreator
    public ALSQuery(@JsonProperty("userId") String userId,
                    @JsonProperty("num") String num
    ) {
        this.userId = userId;
        this.num = num;
    }

    public String getUserId() {
        return userId;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "als";
    }
}
