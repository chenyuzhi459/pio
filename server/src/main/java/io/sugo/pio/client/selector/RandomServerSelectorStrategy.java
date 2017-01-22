package io.sugo.pio.client.selector;

import com.google.common.collect.Iterators;

import java.util.Random;
import java.util.Set;

public class RandomServerSelectorStrategy implements ServerSelectorStrategy
{
  private static final Random random = new Random();

  @Override
  public QueryablePioServer pick(Set<QueryablePioServer> servers)
  {
    return Iterators.get(servers.iterator(), random.nextInt(servers.size()));
  }
}
