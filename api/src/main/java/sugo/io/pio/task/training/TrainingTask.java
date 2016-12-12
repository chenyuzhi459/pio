package sugo.io.pio.task.training;

import com.fasterxml.jackson.annotation.JsonCreator;
import sugo.io.pio.task.Task;

/**
 */
public abstract class TrainingTask implements Task {
    @JsonCreator
    public TrainingTask() {

    }

    @Override
    public void run() {

    }
}
