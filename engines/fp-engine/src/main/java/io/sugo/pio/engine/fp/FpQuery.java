package io.sugo.pio.engine.fp;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FpQuery {
    private String item_id;
    private String num;


    @JsonCreator
    public FpQuery(
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
}
