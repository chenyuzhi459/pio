package io.sugo.pio.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.logger.Logger;
import io.sugo.pio.server.PioNode;
import io.sugo.pio.server.coordination.PioServerMetadata;

/**
 */
public class PioServer implements Comparable
{
  public static final int DEFAULT_PRIORITY = 0;

  private static final Logger log = new Logger(PioServer.class);

  private final Object lock = new Object();

  private final PioServerMetadata metadata;

  public PioServer(
      PioNode node,
      String type
  )
  {
    this(
        node.getHostAndPort(),
        node.getHostAndPort(),
        type,
        DEFAULT_PRIORITY
    );
  }

  @JsonCreator
  public PioServer(
      @JsonProperty("name") String name,
      @JsonProperty("host") String host,
      @JsonProperty("type") String type,
      @JsonProperty("priority") int priority
  )
  {
    this.metadata = new PioServerMetadata(name, host, type, priority);
  }

  public String getName()
  {
    return metadata.getName();
  }

  public PioServerMetadata getMetadata()
  {
    return metadata;
  }

  public String getHost()
  {
    return metadata.getHost();
  }

  public String getType()
  {
    return metadata.getType();
  }

  public int getPriority()
  {
    return metadata.getPriority();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PioServer that = (PioServer) o;

    if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return getName() != null ? getName().hashCode() : 0;
  }

  @Override
  public String toString()
  {
    return metadata.toString();
  }

  @Override
  public int compareTo(Object o)
  {
    if (this == o) {
      return 0;
    }
    if (o == null || getClass() != o.getClass()) {
      return 1;
    }

    return getName().compareTo(((PioServer) o).getName());
  }
}
