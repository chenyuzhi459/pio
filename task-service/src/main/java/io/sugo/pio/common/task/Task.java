package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.query.QueryRunner;

import java.util.Map;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "noop", value = NoopTask.class),
        @JsonSubTypes.Type(name = "model", value = NoopTask.class),
})
public interface Task {
    /**
     * Returns query runners for this task. If this task is not meant to answer queries over its datasource, this method
     * should return null.
     */
    public <Q, R> QueryRunner<Q, R> getQueryRunner();

    /**
     * Returns an extra classpath that should be prepended to the default classpath when running this task. If no
     * extra classpath should be prepended, this should return null or the empty string.
     */
    public String getClasspathPrefix();

    /**
     * Execute preflight actions for a task. This can be used to acquire locks, check preconditions, and so on. The
     * actions must be idempotent, since this method may be executed multiple times. This typically runs on the
     * coordinator. If this method throws an exception, the task should be considered a failure.
     * <p/>
     * This method must be idempotent, as it may be run multiple times per task.
     *
     * @return true if ready, false if not ready yet
     *
     * @throws Exception if the task should be considered a failure
     */
    public boolean isReady() throws Exception;

    /**
     * Returns whether or not this task can restore its progress from its on-disk working directory. Restorable tasks
     * may be started with a non-empty working directory. Tasks that exit uncleanly may still have a chance to attempt
     * restores, meaning that restorable tasks should be able to deal with potentially partially written on-disk state.
     */
    public boolean canRestore();

    /**
     * Asks a task to arrange for its "run" method to exit promptly. This method will only be called if
     * {@link #canRestore()} returns true. Tasks that take too long to stop gracefully will be terminated with
     * extreme prejudice.
     */
    public void stopGracefully();

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

    /**
     * Returns a {@link io.sugo.pio.common.task.TaskResource} for this task. Task resources define specific
     * worker requirements a task may require.
     *
     * @return {@link io.sugo.pio.common.task.TaskResource} for this task
     */
    public TaskResource getTaskResource();

    public Map<String, Object> getContext();

    public Object getContextValue(String key);
}
