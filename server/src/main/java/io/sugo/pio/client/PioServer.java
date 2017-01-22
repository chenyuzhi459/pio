package io.sugo.pio.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.metamx.common.logger.Logger;
import io.sugo.pio.server.PioNode;

/**
 */
public class PioServer implements Comparable
{
  public static final int DEFAULT_PRIORITY = 0;

  private static final Logger log = new Logger(PioServer.class);

  private final String name;
  private final String host;
  private final int priority;

  public PioServer(
      PioNode node
  )
  {
    this(
        node.getHostAndPort(),
        node.getHostAndPort(),
        DEFAULT_PRIORITY
    );
  }

  @JsonCreator
  public PioServer(
      @JsonProperty("name") String name,
      @JsonProperty("host") String host,
      @JsonProperty("priority") int priority
  )
  {
    this.name = name;
    this.host = host;
    this.priority = priority;
  }

  @JsonProperty
  public String getName()
  {
    return name;
  }

  @JsonProperty
  public String getHost()
  {
    return host;
  }

  @JsonProperty
  public int getPriority()
  {
    return priority;
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

    if (priority != that.priority) {
      return false;
    }

    if (host != null ? !host.equals(that.host) : that.host != null) {
      return false;
    }
    if (name != null ? !name.equals(that.name) : that.name != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return getName() != null ? getName().hashCode() : 0;
  }

  public String toString()
  {
    return "PioServer{" +
            "name='" + name + '\'' +
            ", host='" + host + '\'' +
            ", priority='" + priority + '\'' +
            '}';
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
