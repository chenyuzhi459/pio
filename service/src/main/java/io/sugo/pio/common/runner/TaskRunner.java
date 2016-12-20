package io.sugo.pio.common.runner;

import com.google.common.util.concurrent.ListenableFuture;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;

/**
 */
public interface TaskRunner {
    /**
     * Run a task. The returned status should be some kind of completed status.
     *
     * @param task task to run
     *
     * @return task status, eventually
     */
    ListenableFuture<TaskStatus> run(Task task);
}
