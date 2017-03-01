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
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.metadata.MetadataProcessManager;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.ports.Connection;
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
    private ObjectMapper jsonMapper;

    @Inject
    public ProcessManager(@Json ObjectMapper jsonMapper, ProcessManagerConfig config, MetadataProcessManager metadataProcessManager) {
        this.config = config;
        this.jsonMapper = jsonMapper;
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
    }

    public static String getOperatorMetaJson() {
        return operatorMetaJson;
    }

    public static Map<String, OperatorMeta> getOperatorMetaMap() {
        return operatorMetaMap;
    }

    @LifecycleStart
    public void start() {
        I18N.loadLanguageResource();

        operatorMetaMap = OperatorMapHelper.getAllOperatorMetas(jsonMapper);
        try {
            operatorMetaJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(operatorMetaMap.values());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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

        log.info("Create process named %s[with id: %s]  successfully.", process.getName(), process.getId());

        return process;
    }

    public OperatorProcess update(String id, String name, String description, String status) {
        OperatorProcess process = get(id, true);
        if (name != null && name != "") {
            process.setName(name);
        }
        if (description != null && description != "") {
            process.setDescription(description);
        }
        if (status != null && status != "") {
            process.setStatus(Status.valueOf(status));
        }
        process.setUpdateTime(new DateTime());
        metadataProcessManager.update(process);

        log.info("Update process named %s[with id: %s]  successfully.", process.getName(), process.getId());

        return process;
    }

    public OperatorProcess delete(String id) {
        OperatorProcess process = get(id);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setStatus(Status.DELETED);
            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);
            processCache.invalidate(id);

            log.info("Delete process named %s[with id: %s]  successfully.", process.getName(), process.getId());

            return process;
        } else {
            return null;
        }
    }

    public OperatorProcess run(String id) {
        try {
            OperatorProcess process = get(id);
            process.clearStatus();
            queue.offer(process, 10, TimeUnit.SECONDS);
            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);
            log.info("Processes that waiting for running number are:%d", queue.size());
            return process;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OperatorProcess> getAll(boolean all) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(all);
        if (processes == null || processes.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Get all processes[number:%d] from database successfully.", processes.size());

        return processes;
    }

    public OperatorProcess get(String id) {
        return get(id, false);
    }

    public OperatorProcess get(String id, boolean all) {
        loader.setProcessId(id);
        try {
            OperatorProcess process = processCache.get(id, loader);
            if (process == null || (!all && Status.DELETED.equals(process.getStatus()))) {
                processCache.invalidate(id);
                return null;
            }

            log.info("Get process named %s[id:%s] from cache successfully.", process.getName(), id);

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

    public OperatorProcess addOperator(String processId, OperatorDto dto) {
        OperatorProcess process = get(processId, false);
        OperatorMeta meta = operatorMetaMap.get(dto.getOperatorType());
        try {
            Operator operator = (Operator) meta.getType().getType().newInstance();
            operator.setName(meta.getName() + "-" + UUID.randomUUID().toString());
            operator.setxPos(dto.getxPos());
            operator.setyPos(dto.getyPos());
            process.getRootOperator().getExecutionUnit().addOperator(operator);
            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);
            return process;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Operator getOperator(String processId, String operatorId) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            return operator;
        } else {
            return null;
        }
    }

    public OperatorProcess deleteOperator(String processId, String operatorId) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.removeOperator(operatorId);
            metadataProcessManager.update(process);
            return process;
        } else {
            return null;
        }
    }

    public OperatorProcess connect(String processId, Connection dto) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.connect(dto, true);
            metadataProcessManager.update(process);
            process.getRootOperator().getExecutionUnit().transformMetaData();
            return process;
        } else {
            return null;
        }
    }

    public OperatorProcess disconnect(String processId, Connection dto) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.disconnect(dto);
            metadataProcessManager.update(process);
            process.getRootOperator().getExecutionUnit().transformMetaData();
            return process;
        } else {
            return null;
        }
    }

    public Operator updateParameter(String processId, String operatorId, String key, String value) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            operator.setParameter(key, value);
            metadataProcessManager.update(process);
            return operator;
        } else {
            return null;
        }
    }

    public Operator updateOperator(String processId, String operatorId, OperatorDto dto) {
        OperatorProcess process = get(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            if (dto.getFullName() != null) {
                operator.setFullName(dto.getFullName());
            }
            if (dto.getxPos() != null) {
                operator.setxPos(dto.getxPos());
            }
            if (dto.getyPos() != null) {
                operator.setyPos(dto.getyPos());
            }

            metadataProcessManager.update(process);
            return operator;
        } else {
            return null;
        }
    }

    public OperatorProcess getResult(String id) {
        OperatorProcess process = get(id);
        IOContainer result = process.getRootOperator().getResults(true);
        return process;
    }
}
