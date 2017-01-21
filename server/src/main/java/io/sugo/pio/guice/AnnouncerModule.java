package io.sugo.pio.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.curator.announcement.Announcer;
import io.sugo.pio.server.coordination.BatchDataServerAnnouncer;
import io.sugo.pio.server.coordination.DataServerAnnouncer;
import io.sugo.pio.server.coordination.DataServerAnnouncerProvider;
import org.apache.curator.framework.CuratorFramework;

/**
 */
public class AnnouncerModule implements Module
{
  @Override
  public void configure(Binder binder)
  {
    JsonConfigProvider.bind(binder, "pio.announcer", DataServerAnnouncerProvider.class);
    binder.bind(DataServerAnnouncer.class).toProvider(DataServerAnnouncerProvider.class);
    binder.bind(BatchDataServerAnnouncer.class).in(ManageLifecycleLast.class);
  }

  @Provides
  @ManageLifecycle
  public Announcer getAnnouncer(CuratorFramework curator)
  {
    return new Announcer(curator, Execs.singleThreaded("Announcer-%s"));
  }
}
