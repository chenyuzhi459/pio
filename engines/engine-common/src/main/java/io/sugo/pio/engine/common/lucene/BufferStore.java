package io.sugo.pio.engine.common.lucene;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;


public class BufferStore implements Store {
    private static final Store EMPTY = new Store() {
        public byte[] takeBuffer(int bufferSize) {
            return new byte[bufferSize];
        }

        public void putBuffer(byte[] buffer) {
        }
    };
    private static final ConcurrentMap<Integer, BufferStore> bufferStores = new ConcurrentHashMap();
    private final BlockingQueue<byte[]> buffers;
    private final int bufferSize;
    private final AtomicLong shardBuffercacheAllocate;
    private final AtomicLong shardBuffercacheLost;

    private BufferStore(int bufferSize, int count, AtomicLong shardBuffercacheAllocate, AtomicLong shardBuffercacheLost) {
        this.bufferSize = bufferSize;
        this.shardBuffercacheAllocate = shardBuffercacheAllocate;
        this.shardBuffercacheLost = shardBuffercacheLost;
        this.buffers = setupBuffers(bufferSize, count);
    }

    private static BlockingQueue<byte[]> setupBuffers(int bufferSize, int count) {
        ArrayBlockingQueue queue = new ArrayBlockingQueue(count);

        for (int i = 0; i < count; ++i) {
            queue.add(new byte[bufferSize]);
        }

        return queue;
    }

    public static Store instance(int bufferSize) {
        BufferStore bufferStore = bufferStores.get(Integer.valueOf(bufferSize));
        return (bufferStore == null ? EMPTY : bufferStore);
    }

    public byte[] takeBuffer(int bufferSize) {
        if (this.bufferSize != bufferSize) {
            throw new RuntimeException("Buffer with length [" + bufferSize + "] does not match buffer size of [" + bufferSize + "]");
        } else {
            return this.newBuffer((byte[]) this.buffers.poll());
        }
    }

    public void putBuffer(byte[] buffer) {
        if (buffer != null) {
            if (buffer.length != this.bufferSize) {
                throw new RuntimeException("Buffer with length [" + buffer.length + "] does not match buffer size of [" + this.bufferSize + "]");
            } else {
                this.checkReturn(this.buffers.offer(buffer));
            }
        }
    }

    private void checkReturn(boolean accepted) {
        if (!accepted) {
            this.shardBuffercacheLost.incrementAndGet();
        }

    }

    private byte[] newBuffer(byte[] buf) {
        if (buf != null) {
            return buf;
        } else {
            this.shardBuffercacheAllocate.incrementAndGet();
            return new byte[this.bufferSize];
        }
    }
}
