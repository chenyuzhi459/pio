package io.sugo.pio.overlord;

import com.google.common.util.concurrent.ListenableFuture;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;

import java.util.concurrent.Executor;

/**
 */
public interface TaskRunner {
    /**
     * Register a listener with this task runner. On registration, the listener will get events corresponding to the
     * current state of known tasks.
     *
     * Listener notifications are submitted to the executor in the order they occur, but it is up to the executor
     * to decide when to actually run the notifications. If your listeners will not block, feel free to use a
     * same-thread executor. Listeners that may block should use a separate executor, generally a single-threaded
     * one with a fifo queue so the order of notifications is retained.
     *
     * @param listener the listener
     * @param executor executor to run callbacks in
     */
    void registerListener(TaskRunnerListener listener, Executor executor);

    void unregisterListener(String listenerId);

    /**
     * Run a task. The returned status should be some kind of completed status.
     *
     * @param task task to run
     *
     * @return task status, eventually
     */
    ListenableFuture<TaskStatus> run(Task task);

    /**
     * Stop this task runner. This may block until currently-running tasks can be gracefully stopped. After calling
     * stopping, "run" will not accept further tasks.
     */
    void stop();
}
