package io.sugo.pio.engine.bbs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

/**
 */
public class BbsQuery implements PredictionQueryObject {
    private String title;
    private String content;

    @JsonCreator
    public BbsQuery(
            @JsonProperty("title") String title,
            @JsonProperty("content") String content
    ) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getType() {
        return "bbsSimilar";
    }
}
