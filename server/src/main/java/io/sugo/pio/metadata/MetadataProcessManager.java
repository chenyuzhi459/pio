package io.sugo.pio.metadata;

import io.sugo.pio.OperatorProcess;

/**
 */

public interface MetadataProcessManager
{
  void start();

  void stop();

  OperatorProcess get(String id);
  void insert(OperatorProcess spec);
  boolean updateStatus(OperatorProcess pi);
}
