package io.sugo.pio.client.selector;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = RandomServerSelectorStrategy.class)
@JsonSubTypes(value = {
    @JsonSubTypes.Type(name = "random", value = RandomServerSelectorStrategy.class),
    @JsonSubTypes.Type(name = "connectionCount", value = ConnectionCountServerSelectorStrategy.class)
})
public interface ServerSelectorStrategy
{
  public QueryablePioServer pick(Set<QueryablePioServer> servers);
}
