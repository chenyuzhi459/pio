package io.sugo.pio.metadata;

import io.sugo.pio.OperatorProcess;

import java.util.List;

public interface MetadataRecInstanceManager
{
  void start();

  void stop();

  OperatorProcess get(String id);
  OperatorProcess get(String id, boolean includeDelete);
  void insert(OperatorProcess spec);
  boolean update(OperatorProcess pi);

  List<OperatorProcess> getAll(boolean includeDelete);
}
