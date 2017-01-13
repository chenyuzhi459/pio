package io.sugo.pio.engine.als;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AlsQuery {
    private String userId;
    private String num;

    @JsonCreator
    public AlsQuery(@JsonProperty("userId") String userId,
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
}
