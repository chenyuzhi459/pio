package io.sugo.pio.data.fetcher;

import com.metamx.common.RE;
import com.metamx.common.logger.Logger;
import redis.clients.jedis.HostAndPort;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class RedisDataFetcher implements DataFetcher {
    private final static Logger log = new Logger(RedisDataFetcher.class);

    private final static int BATCH_SIZE = 1 * 1024 * 1024;
    private final static int DIRECT_BUFFER_SIZE = 100 * 1024 * 1024;
    private static final int MAX_SIZE = 10;

    private final String hostAndPorts;
    private boolean clusterMode = false;
    private Set<HostAndPort> nodes;
    private BlockingQueue<RedisClientWrapper> queue = new LinkedBlockingQueue<>();
    private final AtomicInteger size = new AtomicInteger();
    private Object lock = new Object();

    public RedisDataFetcher(String hostAndPorts, boolean clusterMode) {
        this.clusterMode = clusterMode;
        this.hostAndPorts = hostAndPorts;
        parseHostAndPorts(hostAndPorts);
    }

    private void parseHostAndPorts(String hostAndPorts) {
        StringTokenizer tokenizer = new StringTokenizer(hostAndPorts, ",;");
        String token;
        String[] tmp;
        this.nodes = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            tmp = token.split(":");
            this.nodes.add(new HostAndPort(tmp[0], Integer.valueOf(tmp[1])));
        }
    }

    public Set<HostAndPort> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "DataRedisIOFactory{" +
                "nodes='" + hostAndPorts + '\'' +
                ", clusterMode='" + clusterMode + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = (hostAndPorts != null ? hostAndPorts.hashCode() : 0);
        result = 31 * result + (clusterMode ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedisDataFetcher that = (RedisDataFetcher) o;
        if (hostAndPorts != null ? !hostAndPorts.equals(that.hostAndPorts) : that.hostAndPorts != null) {
            return false;
        }
        if (clusterMode != that.clusterMode) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] readBytes() {
        String groupId = "";
        long start = System.currentTimeMillis();
        int clientSize = size.get();
        RedisClientWrapper wrapper = getRedisClientWrapper();
        Long listSize = wrapper.llen(groupId);
        if (listSize == 0) {
            releaseRedisClientWrapper(wrapper);
            return new byte[0];
        }

        ByteBuffer buf = ByteBuffer.allocate(DIRECT_BUFFER_SIZE);

        byte[] bytes;
        buf.clear();
        for (int i = 0; i < listSize; i++) {
            byte[] item = wrapper.lindex(groupId, i);
            buf.put(item);
        }

        buf.flip();
        bytes = new byte[buf.limit()];
        buf.get(bytes);
        releaseRedisClientWrapper(wrapper);
        long end = System.currentTimeMillis();
        log.info(String.format("[%s] read redis spendTime:%d data bytes:%d", groupId, end - start, bytes.length));
        return bytes;
    }

    private void releaseRedisClientWrapper(RedisClientWrapper wrapper) {
        try {
            queue.put(wrapper);
        } catch (InterruptedException e) {
            throw new RE("", e);
        }
    }

    private RedisClientWrapper getRedisClientWrapper() {
        RedisClientWrapper wrapper;
        synchronized (lock) {
            if (queue.size() == 0 && size.get() < MAX_SIZE) {
                wrapper = new RedisClientWrapper(clusterMode, getNodes());
                size.incrementAndGet();
            } else {
                try {
                    wrapper = queue.take();
                } catch (InterruptedException e) {
                    throw new RE("", e);
                }
            }
        }

        return wrapper;
    }

    @Override
    public void writeBytes(byte[] buf) {
        String groupId = "";
        RedisClientWrapper wrapper = getRedisClientWrapper();
        byte[] dest = new byte[BATCH_SIZE];
        int srcPos = 0;
        int length = BATCH_SIZE;
        int totalSize = buf.length;
        wrapper.del(groupId);
        while (srcPos < buf.length) {
            if (length > totalSize - srcPos) {
                length = totalSize - srcPos;
                dest = new byte[length];
            }
            System.arraycopy(buf, srcPos, dest, 0, length);
            srcPos += length;
            wrapper.rpush(groupId, dest);
        }
        releaseRedisClientWrapper(wrapper);
    }

    @Override
    public void close() {
        while (queue.size() > 0) {
            try {
                RedisClientWrapper wrapper = queue.poll(3, TimeUnit.SECONDS);
                wrapper.close();
            } catch (InterruptedException e) {
                log.warn("close redis client error", e);
            }
        }
    }

    @Override
    public String[] fetchData(List<String> itemIdSet, String field) {
        RedisClientWrapper wrapper = getRedisClientWrapper();
        try {
            return wrapper.batchGet(itemIdSet, field);
        } finally {
            releaseRedisClientWrapper(wrapper);
        }
    }
}
