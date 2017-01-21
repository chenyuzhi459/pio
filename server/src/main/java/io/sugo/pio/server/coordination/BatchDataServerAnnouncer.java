package io.sugo.pio.server.coordination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import io.sugo.pio.client.PioDataServer;
import io.sugo.pio.client.PioServer;
import io.sugo.pio.curator.announcement.Announcer;
import io.sugo.pio.server.initialization.ZkPathsConfig;
import org.apache.curator.utils.ZKPaths;

import java.io.IOException;

/**
 */
public class BatchDataServerAnnouncer implements DataServerAnnouncer
{
  private static final Logger log = new Logger(BatchDataServerAnnouncer.class);

  private final Announcer announcer;
  private final ObjectMapper jsonMapper;
  private final PioServer server;
  private final ZkPathsConfig config;

  private final Object lock = new Object();

  private volatile boolean announced = false;

  @Inject
  public BatchDataServerAnnouncer(
      PioServer server,
      ZkPathsConfig config,
      Announcer announcer,
      ObjectMapper jsonMapper
  )
  {
    this.announcer = announcer;
    this.jsonMapper = jsonMapper;
    this.server = server;
    this.config = config;
  }

  @Override
  public void announce(String id) throws IOException
  {
    synchronized (lock) {
      if (announced) {
        return;
      }

      try {
        final String path = makeAnnouncementPath();
        log.info("Announcing self[%s] at [%s]", server, path);
        PioDataServer dataServer = new PioDataServer(id, server);
        announcer.announce(path, jsonMapper.writeValueAsBytes(dataServer), false);
      }
      catch (JsonProcessingException e) {
        throw Throwables.propagate(e);
      }

      announced = true;
    }
  }

  @Override
  public void unannounce() throws IOException
  {
    synchronized (lock) {
      if (!announced) {
        return;
      }

      log.info("Stopping %s with config[%s]", getClass(), config);
      announcer.unannounce(makeAnnouncementPath());

      announced = false;
    }
  }

  @Override
  public boolean isAnnounced()
  {
    return announced;
  }

  private String makeAnnouncementPath()
  {
    return ZKPaths.makePath(config.getAnnouncementsPath(), server.getName());
  }
}
