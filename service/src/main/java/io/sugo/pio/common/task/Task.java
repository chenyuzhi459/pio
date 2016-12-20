package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.common.TaskStatus;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "training", value = BatchTrainingTask.class)
})
public interface Task {
    /**
     * Execute a task. This typically runs on a worker as determined by a TaskRunner, and will be run while
     * holding a lock on our dataSource and implicit lock interval (if any). If this method throws an exception, the task
     * should be considered a failure.
     *
     * @return Some kind of finished status (isRunnable must be false).
     *
     * @throws Exception if this task failed
     */
    TaskStatus run() throws Exception;

    /**
     * Returns ID of this task. Must be unique across all tasks ever created.
     *
     * @return task ID
     */
    public String getId();
}
