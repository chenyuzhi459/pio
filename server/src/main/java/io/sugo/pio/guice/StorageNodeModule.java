package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import io.sugo.pio.client.PioServer;
import io.sugo.pio.server.PioNode;

/**
 */
public class StorageNodeModule implements Module
{
  @Override
  public void configure(Binder binder)
  {
  }

  @Provides
  @LazySingleton
  public PioServer getMetadata(@Self PioNode node)
  {
    return new PioServer(
        node.getHostAndPort(),
        node.getHostAndPort(),
        0
    );
  }
}
