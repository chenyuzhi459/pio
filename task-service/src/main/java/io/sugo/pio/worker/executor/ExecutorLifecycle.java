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

package io.sugo.pio.worker.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.metamx.common.ISE;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.config.TaskConfig;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.overlord.TaskRunner;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;

/**
 * Encapsulates the lifecycle of a task executor. Loads one task, runs it, writes its status, and all the while
 * monitors its parent process.
 */
public class ExecutorLifecycle
{
  private final ExecutorLifecycleConfig taskExecutorConfig;
  private final TaskConfig taskConfig;
  private final TaskRunner taskRunner;
  private final ObjectMapper jsonMapper;

  private final ExecutorService parentMonitorExec = Execs.singleThreaded("parent-monitor-%d");

  private volatile Task task = null;
  private volatile ListenableFuture<TaskStatus> statusFuture = null;
  private volatile FileChannel taskLockChannel;
  private volatile FileLock taskLockFileLock;

  @Inject
  public ExecutorLifecycle(
      ExecutorLifecycleConfig taskExecutorConfig,
      TaskConfig taskConfig,
      TaskRunner taskRunner,
      ObjectMapper jsonMapper
  )
  {
    this.taskExecutorConfig = taskExecutorConfig;
    this.taskConfig = taskConfig;
    this.taskRunner = taskRunner;
    this.jsonMapper = jsonMapper;
  }

  @LifecycleStart
  public void start() throws InterruptedException
  {
    final File taskFile = Preconditions.checkNotNull(taskExecutorConfig.getTaskFile(), "taskFile");
    final File statusFile = Preconditions.checkNotNull(taskExecutorConfig.getStatusFile(), "statusFile");
    final InputStream parentStream = Preconditions.checkNotNull(taskExecutorConfig.getParentStream(), "parentStream");

    try {
      task = jsonMapper.readValue(taskFile, Task.class);
    }
    catch (IOException e) {
      throw Throwables.propagate(e);
    }

    // Avoid running the same task twice on the same machine by locking the task base directory.

    final File taskLockFile = taskConfig.getTaskLockFile(task.getId());

    try {
      synchronized (this) {
        if (taskLockChannel == null && taskLockFileLock == null) {
          taskLockChannel = FileChannel.open(
              taskLockFile.toPath(),
              StandardOpenOption.CREATE,
              StandardOpenOption.WRITE
          );

          final long startLocking = System.currentTimeMillis();
          final long timeout = new DateTime(startLocking).plus(taskConfig.getDirectoryLockTimeout()).getMillis();
          while (taskLockFileLock == null && System.currentTimeMillis() < timeout) {
            taskLockFileLock = taskLockChannel.tryLock();
            if (taskLockFileLock == null) {
              Thread.sleep(100);
            }
          }

          if (taskLockFileLock == null) {
            throw new ISE("Could not acquire lock file[%s] within %,dms.", taskLockFile, timeout - startLocking);
          }
        } else {
          throw new ISE("Already started!");
        }
      }
    }
    catch (IOException e) {
      throw Throwables.propagate(e);
    }

    // Spawn monitor thread to keep a watch on parent's stdin
    // If stdin reaches eof, the parent is gone, and we should shut down
    parentMonitorExec.submit(
        new Runnable()
        {
          @Override
          public void run()
          {
            try {
              while (parentStream.read() != -1) {
                // Toss the byte
              }
            }
            catch (Exception e) {
            }

            // Kind of gross, but best way to kill the JVM as far as I know
            System.exit(2);
          }
        }
    );

    // Won't hurt in remote mode, and is required for setting up locks in local mode:
    try {
      if (!task.isReady()) {
        throw new ISE("Task is not ready to run yet!", task.getId());
      }
    }
    catch (Exception e) {
      throw new ISE(e, "Failed to run isReady", task.getId());
    }

    statusFuture = Futures.transform(
        taskRunner.run(task),
        new Function<TaskStatus, TaskStatus>()
        {
          @Override
          public TaskStatus apply(TaskStatus taskStatus)
          {
            try {
              final File statusFileParent = statusFile.getParentFile();
              if (statusFileParent != null) {
                statusFileParent.mkdirs();
              }
              jsonMapper.writeValue(statusFile, taskStatus);

              return taskStatus;
            }
            catch (Exception e) {
              throw Throwables.propagate(e);
            }
          }
        }
    );
  }

  public void join()
  {
    try {
      statusFuture.get();
    }
    catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @LifecycleStop
  public void stop() throws Exception
  {
    parentMonitorExec.shutdown();

    synchronized (this) {
      if (taskLockFileLock != null) {
        taskLockFileLock.release();
      }

      if (taskLockChannel != null) {
        taskLockChannel.close();
      }
    }
  }
}