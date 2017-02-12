package io.sugo.pio.metadata;

import io.sugo.pio.recommend.bean.RecInstance;
import io.sugo.pio.recommend.bean.RecInstanceCriteria;

import java.util.List;

public interface MetadataRecInstanceManager
{
  void start();

  void stop();

  RecInstance get(String id);
  void insert(RecInstance recInstance);
  boolean update(RecInstance entry);

  List<RecInstance> getAll(RecInstanceCriteria criteria);

  void delete(String id);
}
