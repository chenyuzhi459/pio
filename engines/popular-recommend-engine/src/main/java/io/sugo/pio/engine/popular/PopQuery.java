package io.sugo.pio.engine.popular;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PopQuery {
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
}
