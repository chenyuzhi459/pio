package io.sugo.pio.engine.userHistory;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class UserHistoryQuery implements PredictionQueryObject {
    private String user_id;
    private String item_name;
    private String num;


    @JsonCreator
    public UserHistoryQuery(
            @JsonProperty("user_id") String user_id,
            @JsonProperty("item_name") String item_name,
            @JsonProperty("num") String num
    ) {
        this.user_id = user_id;
        this.item_name = item_name;
        this.num = num;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getNum() {
        return num;
    }

    @Override
    public String getType() {
        return "userhistory";
    }
}
