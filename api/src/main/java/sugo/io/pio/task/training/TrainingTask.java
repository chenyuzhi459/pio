package sugo.io.pio.task.training;

import sugo.io.pio.task.Task;

/**
 */
public abstract class TrainingTask implements Task {
    protected String id;
    protected String url;

    public TrainingTask(String id, String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public void run() {

    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}