package io.sugo.pio.client.selector;

import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class ConnectionCountServerSelectorStrategy implements ServerSelectorStrategy
{
  private static final Comparator<QueryablePioServer> comparator = new Comparator<QueryablePioServer>()
  {
    @Override
    public int compare(QueryablePioServer left, QueryablePioServer right)
    {
      return Ints.compare(left.getClient().getNumOpenConnections(), right.getClient().getNumOpenConnections());
    }
  };

  @Override
  public QueryablePioServer pick(Set<QueryablePioServer> servers)
  {
    return Collections.min(servers, comparator);
  }
}
