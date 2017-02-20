package io.sugo.pio.data.fetcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DataFetcherConfig {

    @JsonProperty
    private String type = RedisDataFetcher.REDIS;

    @JsonProperty
    private String hostAndPorts;

    @JsonProperty
    private boolean clusterMode;

    public String getHostAndPorts() {
        return hostAndPorts;
    }

    public void setHostAndPorts(String hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
    }

    public boolean isClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<HostAndPort> getNodes() {
        StringTokenizer tokenizer = new StringTokenizer(hostAndPorts, ",;");
        String token;
        String[] tmp;
        Set<HostAndPort> nodes = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            tmp = token.split(":");
            nodes.add(new HostAndPort(tmp[0], Integer.valueOf(tmp[1])));
        }
        return nodes;
    }

    @Override
    public String toString() {
        return String.format("%s {hostAndPorts=%s, clusterMode=%s}",
                DataFetcherConfig.class.getSimpleName(), hostAndPorts, clusterMode);
    }
}
