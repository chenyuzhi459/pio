package io.sugo.pio.metadata;

import io.sugo.pio.server.process.ProcessInstance;

/**
 */

public interface MetadataProcessInstanceManager
{
  void start();

  void stop();

  void insert(ProcessInstance spec);
  boolean updateStatus(ProcessInstance pi);
}
