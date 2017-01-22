package io.sugo.pio.client.selector;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.TreeMap;

/**
 */
public class ServerSelector implements DiscoverySelector<QueryablePioServer>
{

  private final Set<QueryablePioServer> servers = Sets.newHashSet();

  private final TierSelectorStrategy strategy;

  public ServerSelector(
      TierSelectorStrategy strategy
  )
  {
    this.strategy = strategy;
  }

  public void addServer(
          QueryablePioServer server
  )
  {
    synchronized (this) {
      servers.add(server);
    }
  }

  public boolean removeServer(QueryablePioServer server)
  {
    synchronized (this) {
      return servers.remove(server);
    }
  }

  public boolean isEmpty()
  {
    synchronized (this) {
      return servers.isEmpty();
    }
  }

  public QueryablePioServer pick()
  {
    synchronized (this) {
      final TreeMap<Integer, Set<QueryablePioServer>> prioritizedServers = new TreeMap<>(strategy.getComparator());
      for (QueryablePioServer server : servers) {
        Set<QueryablePioServer> theServers = prioritizedServers.get(server.getServer().getPriority());
        if (theServers == null) {
          theServers = Sets.newHashSet();
          prioritizedServers.put(server.getServer().getPriority(), theServers);
        }
        theServers.add(server);
      }

      return strategy.pick(prioritizedServers);
    }
  }
}
