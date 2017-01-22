/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.sugo.pio.overlord;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.EntryExistsException;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements an in-heap TaskStorage facility, with no persistence across restarts. This class is not
 * thread safe.
 */
public class HeapMemoryTaskStorage implements TaskStorage
{
  private final ReentrantLock giant = new ReentrantLock();
  private final Map<String, TaskStuff> tasks = Maps.newHashMap();

  private static final Logger log = new Logger(HeapMemoryTaskStorage.class);

  @Override
  public void insert(Task task, TaskStatus status) throws EntryExistsException
  {
    giant.lock();

    try {
      Preconditions.checkNotNull(task, "task");
      Preconditions.checkNotNull(status, "status");
      Preconditions.checkArgument(
          task.getId().equals(status.getId()),
          "Task/Status ID mismatch[%s/%s]",
          task.getId(),
          status.getId()
      );

      if(tasks.containsKey(task.getId())) {
        throw new EntryExistsException(task.getId());
      }

      log.info("Inserting task %s with status: %s", task.getId(), status);
      tasks.put(task.getId(), new TaskStuff(task, status, new DateTime()));
    } finally {
      giant.unlock();
    }
  }

  @Override
  public void setStatus(TaskStatus status)
  {
    giant.lock();

    try {
      Preconditions.checkNotNull(status, "status");

      final String taskid = status.getId();
      Preconditions.checkState(tasks.containsKey(taskid), "Task ID must already be present: %s", taskid);
      Preconditions.checkState(tasks.get(taskid).getStatus().isRunnable(), "Task status must be runnable: %s", taskid);
      log.info("Updating task %s to status: %s", taskid, status);
      tasks.put(taskid, tasks.get(taskid).withStatus(status));
    } finally {
      giant.unlock();
    }
  }

  @Override
  public Optional<TaskStatus> getStatus(String taskid)
  {
    giant.lock();

    try {
      Preconditions.checkNotNull(taskid, "taskid");
      if(tasks.containsKey(taskid)) {
        return Optional.of(tasks.get(taskid).getStatus());
      } else {
        return Optional.absent();
      }
    } finally {
      giant.unlock();
    }
  }

  @Override
  public List<Task> getActiveTasks()
  {
    giant.lock();

    try {
      final ImmutableList.Builder<Task> listBuilder = ImmutableList.builder();
      for(final TaskStuff taskStuff : tasks.values()) {
        if(taskStuff.getStatus().isRunnable()) {
          listBuilder.add(taskStuff.getTask());
        }
      }
      return listBuilder.build();
    } finally {
      giant.unlock();
    }
  }

  private static class TaskStuff
  {
    final Task task;
    final TaskStatus status;
    final DateTime createdDate;

    private TaskStuff(Task task, TaskStatus status, DateTime createdDate)
    {
      Preconditions.checkNotNull(task);
      Preconditions.checkNotNull(status);
      Preconditions.checkArgument(task.getId().equals(status.getId()));

      this.task = task;
      this.status = status;
      this.createdDate = Preconditions.checkNotNull(createdDate, "createdDate");
    }

    public Task getTask()
    {
      return task;
    }

    public TaskStatus getStatus()
    {
      return status;
    }

    public DateTime getCreatedDate()
    {
      return createdDate;
    }

    private TaskStuff withStatus(TaskStatus _status)
    {
      return new TaskStuff(task, _status, createdDate);
    }
  }
}
