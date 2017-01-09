package io.sugo.pio.engine.ur.detail;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class URDetailQuery {
    private String userId;
    private String detailCategory;
    private String num;

    @JsonCreator
    public URDetailQuery(@JsonProperty("userId") String userId,
                         @JsonProperty("detailCategory") String detailCategory,
                         @JsonProperty("num") String num
    ) {
        this.detailCategory = detailCategory;
        this.userId = userId;
        this.num = num;
    }

    public String getUserId() {
        return userId;
    }

    public String getDetailCategory() {
        return detailCategory;
    }

    public String getNum() {
        return num;
    }
}
