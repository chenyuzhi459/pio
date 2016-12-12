package sugo.io.pio.task.training;

import com.fasterxml.jackson.annotation.JsonCreator;
import sugo.io.pio.engine.Engine;
import sugo.io.pio.task.Task;

/**
 */
public abstract class TrainingTask implements Task {
    private Engine engine;

    @JsonCreator
    public TrainingTask() {

    }

    @Override
    public void run() {

    }
}
