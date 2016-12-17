package sugo.io.pio.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sugo.io.pio.task.training.BatchTrainingTask;
import sugo.io.pio.task.training.TrainingTask;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "training", value = BatchTrainingTask.class)
})
public interface Task {
    void run();
}
