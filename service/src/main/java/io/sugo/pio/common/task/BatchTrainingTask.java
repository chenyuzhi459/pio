package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.data.input.BatchEventHose;

/**
 */
public class BatchTrainingTask extends TrainingTask {
    @JsonProperty("eventHose")
    private BatchEventHose eventHose;

    @JsonCreator
    public BatchTrainingTask(@JsonProperty("id") String id,
                             @JsonProperty("url") String url,
                             @JsonProperty("eventHose") BatchEventHose eventHose) {
        super(id, url);
    }
}
