package io.sugo.pio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.server.initialization.ZkPathsConfig;
import org.apache.curator.framework.CuratorFramework;

/**
 */
@ManageLifecycle
public class BatchServerInventoryView extends ServerInventoryView
    implements FilteredServerInventoryView
{
  @Inject
  public BatchServerInventoryView(
      final ZkPathsConfig zkPaths,
      final CuratorFramework curator,
      final ObjectMapper jsonMapper
  )
  {
    super(
        zkPaths.getAnnouncementsPath(),
        curator,
        jsonMapper
    );
  }
}
