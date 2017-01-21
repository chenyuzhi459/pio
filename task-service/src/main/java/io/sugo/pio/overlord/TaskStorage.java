package io.sugo.pio.overlord;

import com.google.common.base.Optional;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.EntryExistsException;

import java.util.List;

/**
 */
public interface TaskStorage {
    /**
     * Adds a task to the storage facility with a particular status.
     *
     * @param task task to add
     * @param status task status
     * @throws io.sugo.pio.metadata.EntryExistsException if the task ID already exists
     */
    public void insert(Task task, TaskStatus status) throws EntryExistsException;

    /**
     * Persists task status in the storage facility. This method should throw an exception if the task status lifecycle
     * is not respected (absent -&gt; RUNNING -&gt; SUCCESS/FAILURE).
     *
     * @param status task status
     */
    public void setStatus(TaskStatus status);

    /**
     * Returns task status as stored in the storage facility. If the task ID does not exist, this will return
     * an absentee Optional.
     *
     * @param taskid task ID
     * @return task status
     */
    public Optional<TaskStatus> getStatus(String taskid);

    /**
     * Returns a list of currently running or pending tasks as stored in the storage facility. No particular order
     * is guaranteed, but implementations are encouraged to return tasks in ascending order of creation.
     *
     * @return list of active tasks
     */
    public List<Task> getActiveTasks();
}
