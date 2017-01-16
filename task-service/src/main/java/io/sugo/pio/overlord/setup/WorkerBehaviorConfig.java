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

package io.sugo.pio.overlord.setup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class WorkerBehaviorConfig
{
  public static final String CONFIG_KEY = "worker.config";
  public static WorkerSelectStrategy DEFAULT_STRATEGY = new FillCapacityWorkerSelectStrategy();

  public static WorkerBehaviorConfig defaultConfig()
  {
    return new WorkerBehaviorConfig(DEFAULT_STRATEGY);
  }

  private final WorkerSelectStrategy selectStrategy;

  @JsonCreator
  public WorkerBehaviorConfig(
      @JsonProperty("selectStrategy") WorkerSelectStrategy selectStrategy
  )
  {
    this.selectStrategy = selectStrategy;
  }

  @JsonProperty
  public WorkerSelectStrategy getSelectStrategy()
  {
    return selectStrategy;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WorkerBehaviorConfig that = (WorkerBehaviorConfig) o;

    if (selectStrategy != null ? !selectStrategy.equals(that.selectStrategy) : that.selectStrategy != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = selectStrategy != null ? selectStrategy.hashCode() : 0;
    return result;
  }

  @Override
  public String toString()
  {
    return "WorkerConfiguration{" +
           "selectStrategy=" + selectStrategy +
           '}';
  }
}
