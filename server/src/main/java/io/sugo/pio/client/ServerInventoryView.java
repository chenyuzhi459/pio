package io.sugo.pio.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.MapMaker;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.concurrent.Execs;
import io.sugo.pio.curator.inventory.CuratorInventoryManager;
import io.sugo.pio.curator.inventory.CuratorInventoryManagerStrategy;
import io.sugo.pio.curator.inventory.InventoryManagerConfig;
import org.apache.curator.framework.CuratorFramework;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public abstract class ServerInventoryView implements ServerView, InventoryView
{
  private static final Logger log = new Logger(ServerInventoryView.class);

  private final CuratorInventoryManager<PioServer> inventoryManager;
  private final AtomicBoolean started = new AtomicBoolean(false);

  private final ConcurrentMap<ServerCallback, Executor> serverCallbacks = new MapMaker().makeMap();

  public ServerInventoryView(
      final String announcementsPath,
      final CuratorFramework curator,
      final ObjectMapper jsonMapper
  )
  {
    this.inventoryManager = new CuratorInventoryManager<>(
        curator,
        new InventoryManagerConfig()
        {
          @Override
          public String getContainerPath()
          {
            return announcementsPath;
          }
        },
        Execs.singleThreaded("ServerInventoryView-%s"),
        new CuratorInventoryManagerStrategy<PioServer>()
        {
          @Override
          public PioServer deserializeContainer(byte[] bytes)
          {
            try {
              return jsonMapper.readValue(bytes, PioServer.class);
            }
            catch (IOException e) {
              throw Throwables.propagate(e);
            }
          }

          @Override
          public void newContainer(PioServer container)
          {
            log.info("New Server[%s]", container);
            runServerCallbacks(new Function<ServerCallback, CallbackAction>()
            {
              @Override
              public CallbackAction apply(ServerCallback input)
              {
                return input.serverAdded(container);
              }
            });
          }

          @Override
          public void deadContainer(PioServer deadContainer)
          {
            log.info("Server Disappeared[%s]", deadContainer);
            runServerCallbacks(new Function<ServerCallback, CallbackAction>()
            {
              @Override
              public CallbackAction apply(ServerCallback input)
              {
                return input.serverRemoved(deadContainer);
              }
            });
          }

          @Override
          public PioServer updateContainer(PioServer oldContainer, PioServer newContainer)
          {
            log.info("Server updated[%s]", newContainer);
            runServerCallbacks(new Function<ServerCallback, CallbackAction>()
            {
              @Override
              public CallbackAction apply(ServerCallback input)
              {
                return input.serverUpdated(oldContainer, newContainer);
              }
            });
            return newContainer;
          }

          @Override
          public void inventoryInitialized()
          {
            log.info("Inventory Initialized");
          }
        }
    );
  }

  @LifecycleStart
  public void start() throws Exception
  {
    synchronized (started) {
      if (!started.get()) {
        inventoryManager.start();
        started.set(true);
      }
    }
  }

  @LifecycleStop
  public void stop() throws IOException
  {
    synchronized (started) {
      if (started.getAndSet(false)) {
        inventoryManager.stop();
      }
    }
  }

  public boolean isStarted()
  {
    return started.get();
  }

  @Override
  public PioServer getInventoryValue(String containerKey)
  {
    return inventoryManager.getInventoryValue(containerKey);
  }

  @Override
  public Iterable<PioServer> getInventory()
  {
    return inventoryManager.getInventory();
  }

  @Override
  public void registerServerCallback(Executor exec, ServerCallback callback)
  {
    serverCallbacks.put(callback, exec);
  }

  public InventoryManagerConfig getInventoryManagerConfig()
  {
    return inventoryManager.getConfig();
  }

  protected void runServerCallbacks(final Function<ServerCallback, CallbackAction> fn)
  {
    for (final Map.Entry<ServerCallback, Executor> entry : serverCallbacks.entrySet()) {
      entry.getValue().execute(
          new Runnable()
          {
            @Override
            public void run()
            {
              if (CallbackAction.UNREGISTER == fn.apply(entry.getKey())) {
                serverCallbacks.remove(entry.getKey());
              }
            }
          }
      );
    }
  }
}
