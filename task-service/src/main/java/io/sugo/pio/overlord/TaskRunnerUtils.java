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

import com.metamx.common.Pair;
import io.sugo.pio.common.TaskLocation;
import io.sugo.pio.common.TaskStatus;

import java.util.concurrent.Executor;

public class TaskRunnerUtils
{
  public static void notifyLocationChanged(
      final Iterable<Pair<TaskRunnerListener, Executor>> listeners,
      final String taskId,
      final TaskLocation location
  )
  {
    for (final Pair<TaskRunnerListener, Executor> listener : listeners) {
      try {
        listener.rhs.execute(
            new Runnable()
            {
              @Override
              public void run()
              {
                listener.lhs.locationChanged(taskId, location);
              }
            }
        );
      }
      catch (Exception e) {
      }
    }
  }

  public static void notifyStatusChanged(
      final Iterable<Pair<TaskRunnerListener, Executor>> listeners,
      final String taskId,
      final TaskStatus status
  )
  {
    for (final Pair<TaskRunnerListener, Executor> listener : listeners) {
      try {
        listener.rhs.execute(
            new Runnable()
            {
              @Override
              public void run()
              {
                listener.lhs.statusChanged(taskId, status);
              }
            }
        );
      }
      catch (Exception e) {
      }
    }
  }
}
