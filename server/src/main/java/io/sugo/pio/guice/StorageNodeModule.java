package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.util.Providers;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.server.coordination.PioServerMetadata;

import javax.annotation.Nullable;

/**
 */
public class StorageNodeModule implements Module
{
  @Override
  public void configure(Binder binder)
  {
    binder.bind(NodeTypeConfig.class).toProvider(Providers.<NodeTypeConfig>of(null));
  }

  @Provides
  @LazySingleton
  public PioServerMetadata getMetadata(@Self PioNode node, @Nullable NodeTypeConfig nodeType)
  {
    if (nodeType == null) {
      throw new ProvisionException("Must override the binding for NodeTypeConfig if you want a PioServerMetadata.");
    }

    return new PioServerMetadata(
        node.getHostAndPort(),
        node.getHostAndPort(),
        nodeType.getNodeType(),
        0
    );
  }
}
