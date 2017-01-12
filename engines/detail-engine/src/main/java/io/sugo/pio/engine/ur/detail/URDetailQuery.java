package io.sugo.pio.engine.ur.detail;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class URDetailQuery {
    private String itemId;
    private String num;

    @JsonCreator
    public URDetailQuery(@JsonProperty("itemId") String itemId,
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
