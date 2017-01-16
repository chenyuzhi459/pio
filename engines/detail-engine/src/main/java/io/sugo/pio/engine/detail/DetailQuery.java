package io.sugo.pio.engine.detail;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DetailQuery {
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
}
