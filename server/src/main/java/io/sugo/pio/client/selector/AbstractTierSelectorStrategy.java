package io.sugo.pio.client.selector;

import com.metamx.common.ISE;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 */
public abstract class AbstractTierSelectorStrategy implements TierSelectorStrategy
{
  private final ServerSelectorStrategy serverSelectorStrategy;

  public AbstractTierSelectorStrategy(ServerSelectorStrategy serverSelectorStrategy)
  {
    this.serverSelectorStrategy = serverSelectorStrategy;
  }

  @Override
  public QueryablePioServer pick(TreeMap<Integer, Set<QueryablePioServer>> prioritizedServers)
  {
    final Map.Entry<Integer, Set<QueryablePioServer>> priorityServers = prioritizedServers.pollFirstEntry();

    if (priorityServers == null) {
      return null;
    }

    final Set<QueryablePioServer> servers = priorityServers.getValue();
    switch (servers.size()) {
      case 0:
        throw new ISE("[%s] Something hella weird going on here. We should not be here");
      case 1:
        return priorityServers.getValue().iterator().next();
      default:
        return serverSelectorStrategy.pick(servers);
    }
  }
}
