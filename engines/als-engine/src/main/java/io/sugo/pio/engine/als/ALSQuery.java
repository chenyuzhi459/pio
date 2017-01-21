package io.sugo.pio.engine.als;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ALSQuery {
    private String user_id;
    private String num;

    @JsonCreator
    public ALSQuery(@JsonProperty("user_id") String user_id,
                    @JsonProperty("num") String num
    ) {
        this.user_id = user_id;
        this.num = num;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNum() {
        return num;
    }
}
