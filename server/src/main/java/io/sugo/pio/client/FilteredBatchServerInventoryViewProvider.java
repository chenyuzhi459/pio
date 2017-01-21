package io.sugo.pio.client;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;
import io.sugo.pio.server.initialization.ZkPathsConfig;
import org.apache.curator.framework.CuratorFramework;

import javax.validation.constraints.NotNull;

public class FilteredBatchServerInventoryViewProvider implements Provider<FilteredServerInventoryView>
{
  @JacksonInject
  @NotNull
  private ZkPathsConfig zkPaths = null;

  @JacksonInject
  @NotNull
  private CuratorFramework curator = null;

  @JacksonInject
  @NotNull
  private ObjectMapper jsonMapper = null;

  @Override
  public BatchServerInventoryView get()
  {
    return new BatchServerInventoryView(
        zkPaths,
        curator,
        jsonMapper
    );
  }
}
