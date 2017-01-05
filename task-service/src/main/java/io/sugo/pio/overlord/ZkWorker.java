package io.sugo.pio.overlord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import io.sugo.pio.worker.Worker;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 */
public class ZkWorker implements Closeable {
    private final PathChildrenCache statusCache;

    private AtomicReference<Worker> worker;
    private AtomicReference<DateTime> lastCompletedTaskTime = new AtomicReference<DateTime>(new DateTime());

    public ZkWorker(Worker worker, PathChildrenCache statusCache, final ObjectMapper jsonMapper)
    {
        this.worker = new AtomicReference<>(worker);
        this.statusCache = statusCache;
    }

    public void start() throws Exception
    {
        statusCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
    }

    @Override
    public void close() throws IOException
    {
        statusCache.close();
    }

    @Override
    public String toString()
    {
        return "ZkWorker{" +
                "worker=" + worker +
                ", lastCompletedTaskTime=" + lastCompletedTaskTime +
                '}';
    }
}
