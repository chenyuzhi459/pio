package io.sugo.pio.data.fetcher;

import com.metamx.common.RE;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RedisClientWrapper {
    private Jedis jedis;
    private JedisCluster cluster;
    private final boolean clusterMode;
    private Pipeline pipe;

    public RedisClientWrapper(boolean clusterMode, Set<HostAndPort> nodes) {
        this.clusterMode = clusterMode;
        if (this.clusterMode) {
            if (cluster == null) {
                cluster = new JedisCluster(nodes, 10000, 10);
            }
        } else {
            if (jedis == null) {
                HostAndPort node = nodes.iterator().next();
                jedis = new Jedis(node.getHost(), node.getPort(), 10000, 10000);
                pipe = jedis.pipelined();
            }
        }
    }

    public Long rpush(String listKey, byte[] dest) {
        if (clusterMode) {
            return cluster.rpush(SafeEncoder.encode(listKey), dest);
        } else {
            return jedis.rpush(SafeEncoder.encode(listKey), dest);
        }
    }

    public Long llen(String listKey) {
        if (clusterMode) {
            return cluster.llen(listKey);
        } else {
            return jedis.llen(listKey);
        }
    }

    public byte[] lindex(String listKey, long idx) {
        if (clusterMode) {
            return cluster.lindex(SafeEncoder.encode(listKey), idx);
        } else {
            return jedis.lindex(SafeEncoder.encode(listKey), idx);
        }
    }


    public Long del(String listKey) {
        if (clusterMode) {
            return cluster.del(listKey);
        } else {
            return jedis.del(listKey);
        }
    }

    public void close() {
        if (clusterMode) {
            if (cluster != null) {
                try {
                    cluster.close();
                } catch (IOException e) {
                    throw new RE("", e);
                }
            }
        } else {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String[] batchGet(List<String> itemIdSet, String field) {
        if (clusterMode) {
            String[] fieldValues = new String[itemIdSet.size()];
            List<String> values;
            for (int i = 0; i < fieldValues.length; i++) {
                values = cluster.hmget(itemIdSet.get(i), field);
                fieldValues[i] = values.get(0);
            }
            return fieldValues;
        } else {
            List<Response<List<String>>> responseList = new ArrayList<>(itemIdSet.size());
            Response<List<String>> value;
            for (String itemId : itemIdSet) {
                value = pipe.hmget(itemId, field);
                responseList.add(value);
            }
            pipe.sync();
            String[] fieldValues = new String[itemIdSet.size()];
            for (int i = 0; i < fieldValues.length; i++) {
                fieldValues[i] = responseList.get(i).get().get(0);
            }
            return fieldValues;
        }
    }
}
