package sugo.io.pio.task.training;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class BatchTrainingTask extends TrainingTask {

    @JsonCreator
    public BatchTrainingTask(@JsonProperty("id") String id, @JsonProperty("url") String url) {
        super(id, url);

    }
}