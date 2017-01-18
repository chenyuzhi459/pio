package io.sugo.pio.server.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.metadata.MetadataProcessManager;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.server.http.dto.OperatorDto;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@ManageLifecycle
public class ProcessManager {

    private static final Logger log = new Logger(ProcessManager.class);

    private final BlockingQueue<OperatorProcess> queue;
    private final ProcessRunner[] runners;
    private final ProcessManagerConfig config;
    private final int executeMaxThread;
    private final Cache<String, OperatorProcess> processCache;
    private final MetadataProcessManager metadataProcessManager;
    private final OperatorProcessLoader loader;

    private static String operatorMetaJson;
    private static Map<String, OperatorMeta> operatorMetaMap;

    @Inject
    public ProcessManager(@Json ObjectMapper jsonMapper, ProcessManagerConfig config, MetadataProcessManager metadataProcessManager) {
        this.config = config;
        this.metadataProcessManager = metadataProcessManager;
        this.executeMaxThread = config.getExecuteMaxThread();
        queue = new ArrayBlockingQueue<>(config.getProcessQueueSize());
        this.runners = new ProcessRunner[config.getExecuteMaxThread()];
        CacheBuilder builder = CacheBuilder.newBuilder()
                .recordStats()
                .maximumSize(config.getMaxEntriesSize())
                .removalListener(new RemovalListener<String, OperatorProcess>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, OperatorProcess> notification) {
                        OperatorProcess process = notification.getValue();
                        log.info("delete Process %s[%s] status:[%s] from cache", process.getName(), process.getId(), process.getStatus());
                    }
                });
        processCache = builder.build();

        for (int i = 0; i < executeMaxThread; i++) {
            this.runners[i] = new ProcessRunner(queue, i, processCache, metadataProcessManager);
        }
        loader = new OperatorProcessLoader(this.metadataProcessManager);
        if (operatorMetaMap == null) {
            operatorMetaMap = OperatorMapHelper.getAllOperatorMetas(jsonMapper);
            try {
                operatorMetaJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(operatorMetaMap.values());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getOperatorMetaJson() {
        return operatorMetaJson;
    }

    public static Map<String, OperatorMeta> getOperatorMetaMap() {
        return operatorMetaMap;
    }

    @LifecycleStart
    public void start() {
        for (int i = 0; i < config.getExecuteMaxThread(); i++) {
            this.runners[i].start();
        }
    }

    @LifecycleStop
    public void stop() {
        ExecutorService threadPool = Executors.newFixedThreadPool(executeMaxThread);
        Future<Boolean>[] futures = new Future[executeMaxThread];
        for (int i = 0; i < executeMaxThread; i++) {
            futures[i] = threadPool.submit(new ProcessRunnerShutdownHandler(runners[i]));
        }
        for (int i = 0; i < executeMaxThread; i++) {
            try {
                futures[i].get();
            } catch (InterruptedException e) {
                log.error(e, "shutdown processRunner error");
            } catch (ExecutionException e) {
                log.error(e, "shutdown processRunner error");
            }
        }
        processCache.cleanUp();
    }

    class ProcessRunnerShutdownHandler implements Callable<Boolean> {
        private ProcessRunner runner;

        ProcessRunnerShutdownHandler(ProcessRunner runner) {
            this.runner = runner;
        }

        @Override
        public Boolean call() throws Exception {
            runner.shutdown();
            return Boolean.TRUE;
        }
    }

    public OperatorProcess create(String name, String description) {
        OperatorProcess process = new OperatorProcess(name);
        process.setDescription(description);
        processCache.put(process.getId(), process);
        metadataProcessManager.insert(process);
        return process;
    }

    public OperatorProcess update(String id, String name, String description) {
        OperatorProcess process = get(id);
        if (name != null && name != "") {
            process.setName(name);
        }
        if (description != null && description != "") {
            process.setDescription(description);
        }
        process.setUpdateTime(new DateTime());
        metadataProcessManager.update(process);
        return process;
    }

    public OperatorProcess delete(String id) {
        OperatorProcess process = get(id);
        if (process != null && Status.DELETED.equals(process.getStatus())) {
            process.setStatus(Status.DELETED);
            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);
            processCache.invalidate(id);
            return process;
        } else {
            return null;
        }
    }

    public String register(OperatorProcess process) {
        try {
            processCache.put(process.getId(), process);
            metadataProcessManager.insert(process);
            queue.offer(process, 10, TimeUnit.SECONDS);
            log.info("queue size:%d", queue.size());
            return process.getId();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OperatorProcess> getAll() {
        List<OperatorProcess> processes = metadataProcessManager.getAll();
        if (processes == null || processes.isEmpty()) {
            return new ArrayList<>();
        }
        return processes;
    }

    public OperatorProcess get(String id) {
        loader.setProcessId(id);
        try {
            OperatorProcess process = processCache.get(id, loader);
            if (process == null || Status.DELETED.equals(process.getStatus())) {
                processCache.invalidate(id);
                return null;
            }
            return process;
        } catch (ExecutionException e) {
            log.error(e, "get process %s error", id);
            throw new RuntimeException(e);
        }
    }

    class OperatorProcessLoader implements Callable<OperatorProcess> {

        private String processId;
        private final MetadataProcessManager metadataProcessManager;

        public OperatorProcessLoader(MetadataProcessManager metadataProcessManager) {
            this.metadataProcessManager = metadataProcessManager;
        }

        @Override
        public OperatorProcess call() throws Exception {
            OperatorProcess process = metadataProcessManager.get(processId, true);
            return process;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

    }

    public Operator addOperator(OperatorDto dto) {
        String processId = dto.getProcessId();
        OperatorProcess process = get(processId);
        OperatorMeta meta = operatorMetaMap.get(dto.getOperatorType());
        try {
            Operator operator = (Operator) meta.getType().getType().newInstance();
            operator.setName(meta.getName() + "-" + UUID.randomUUID().toString());
            operator.setxPos(dto.getxPos());
            operator.setyPos(dto.getyPos());
            process.getRootOperator().getExecutionUnit(0).addOperator(operator);
            return operator;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
