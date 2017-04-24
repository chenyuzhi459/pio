package io.sugo.pio.server.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.constant.ProcessConstant;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.metadata.MetadataProcessManager;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.ports.Connection;
import io.sugo.pio.server.http.dto.OperatorDto;
import io.sugo.pio.server.http.dto.OperatorParamDto;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

@ManageLifecycle
public class ProcessManager {

    private static final Logger log = new Logger(ProcessManager.class);

    private static final int WAITING_PROCESS_RUN = 100; //million seconds

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
        I18N.loadResources();

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

    public void add2cache(OperatorProcess operatorProcess) {
        processCache.put(operatorProcess.getId(), operatorProcess);
    }

    public OperatorProcess create(String tenantId, String name, String description) {
        return create(tenantId, name, description, null, ProcessConstant.IsTemplate.NO, ProcessConstant.IsCase.NO);
    }

    public OperatorProcess create(String tenantId, String name, String description, String type, int isTemplate, int isCase) {
        OperatorProcess process = new OperatorProcess(name);
        process.setTenantId(tenantId);
        process.setDescription(description);
        process.setType(type);
        process.setIsTemplate(isTemplate);
        process.setIsCase(isCase);
        processCache.put(process.getId(), process);
        metadataProcessManager.insert(process);

        log.info("Create process named %s[with id: %s]  successfully.", process.getName(), process.getId());

        return process;
    }

    /**
     * Create process by given type template
     *
     * @param tenantId tenant id of the company
     * @param type     process type
     * @return new process
     */
    public OperatorProcess createByTemplate(String tenantId, String type) {
        OperatorProcess template = getTemplate(type);
        Preconditions.checkNotNull(template, I18N.getMessage("pio.error.process.not_found_template"), type);

        OperatorProcess newProcess = new OperatorProcess(template.getName());
        newProcess.setTenantId(tenantId);
        newProcess.setType(type);
        newProcess.setDescription("Created by template " + type);
        newProcess.setBuiltIn(ProcessConstant.BuiltIn.YES);
        newProcess.setIsTemplate(ProcessConstant.IsTemplate.NO);

        cloneProcess(newProcess, template);
        log.info("Create process of tenantId[%s] with template of type[%s] successfully.", tenantId, type);

        return newProcess;
    }

    public OperatorProcess cloneCase(String tenantId, String caseId, String name, String description) {
        OperatorProcess originCase = metadataProcessManager.get(caseId, false);
        Preconditions.checkNotNull(originCase, I18N.getMessage("pio.error.process.not_found_case"), caseId);

        OperatorProcess newProcess = new OperatorProcess(originCase.getName());
        newProcess.setTenantId(tenantId);
        newProcess.setName(name);
        newProcess.setDescription(description);
        newProcess.setBuiltIn(ProcessConstant.BuiltIn.NO);
        newProcess.setIsTemplate(ProcessConstant.IsTemplate.NO);
        newProcess.setIsCase(ProcessConstant.IsCase.NO);
        newProcess.setType(originCase.getId()); // The type is the caseId

        cloneProcess(newProcess, originCase);
        log.info("Create case of tenantId[%s] with origin case[%s] successfully.", tenantId, originCase.getName());

        return newProcess;
    }

    public OperatorProcess update(String id, String name, String description, String status) {
        OperatorProcess process = getFromCache(id, true);
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
        OperatorProcess process = getFromCache(id);
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

    public OperatorProcess runAsyn(String id) {
        return runAsyn(id, null, null);
    }

    public OperatorProcess runAsyn(String id, String startOperatorId, String endOperatorId) {
        try {
            OperatorProcess process = getFromCache(id);
            if (Strings.isNullOrEmpty(startOperatorId)) {
                process.clearStatus(); // Not clear the status only in the case of running from given operator
            }
            process.getRootOperator().getExecutionUnit().setStartAndEnd(startOperatorId, endOperatorId);

            log.info("Put the will be running process[id:%s] to the queue.", id);
            queue.offer(process, 10, TimeUnit.SECONDS);

            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);

            log.info("Processes that waiting for running number are:%d", queue.size());

            return process;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public OperatorProcess runSyn(String id) {
        OperatorProcess process = runAsyn(id);
        while (!process.runFinished()) {
            try {
                sleep(WAITING_PROCESS_RUN);
            } catch (InterruptedException ignore) {
            }

            process = getResult(id);
        }

        return process;
    }

    /**
     * Get all processes include template, and exclude built-in processes from database.
     *
     * @param tenantId      tenant id of the company
     * @param includeDelete indicates is include 'DELETED' status processes
     * @return process list
     */
    public List<OperatorProcess> getAll(String tenantId, boolean includeDelete) {
        return getAll(tenantId, includeDelete, ProcessConstant.BuiltIn.NO, null);
    }

    public List<OperatorProcess> getAll(String tenantId, boolean includeDelete, int builtIn, String type) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(tenantId, includeDelete, builtIn, null, null, type, null);
        if (processes == null || processes.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Get all processes[number:%d] from database successfully.", processes.size());

        return processes;
    }

    public List<OperatorProcess> getAllCases(boolean includeDelete) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(null, includeDelete, ProcessConstant.BuiltIn.NO,
                null, ProcessConstant.IsCase.YES, null, null);
        if (processes == null || processes.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("Get all cases[number:%d] from database successfully.", processes.size());

        return processes;
    }

    /**
     * Get process by id exclude 'DELETED' from cache
     *
     * @param id process id
     * @return specified process
     */
    public OperatorProcess getFromCache(String id) {
        return getFromCache(id, false);
    }

    /**
     * Get process by id and status 'DELETED' from cache
     *
     * @param id            process id
     * @param includeDelete true:include 'DELETED' process
     * @return specified process
     */
    public OperatorProcess getFromCache(String id, boolean includeDelete) {
        loader.setProcessId(id);
        try {
            OperatorProcess process = processCache.get(id, loader);
            if (process == null || (!includeDelete && Status.DELETED.equals(process.getStatus()))) {
                processCache.invalidate(id);
                return null;
            }
            log.info("Get process named %s[id:%s] from cache successfully.", process.getName(), id);

            return process;
        } catch (Throwable e) {
            log.error(e, "Get process %s error", id);
            throw new RuntimeException(e);
        }
    }

    public OperatorProcess getFromCache(String tenantId, String type) {
        ConcurrentMap<String, OperatorProcess> map = processCache.asMap();
        if (map != null) {
            Collection<OperatorProcess> collection = map.values();
            if (!collection.isEmpty()) {
                Iterator<OperatorProcess> it = collection.iterator();
                while (it.hasNext()) {
                    OperatorProcess operatorProcess = it.next();
                    if (tenantId.equals(operatorProcess.getTenantId()) && type.equals(operatorProcess.getType())) {
                        return operatorProcess;
                    }
                }
            }
        }

        return null;
    }

    public OperatorProcess get(String tenantId, int builtIn, String type) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(tenantId, false,
                builtIn, ProcessConstant.IsTemplate.NO, null, type, null);
        if (processes == null || processes.isEmpty()) {
            return null;
        }
        log.info("Get process named[%s] from database successfully.", processes.get(0).getName());

        return processes.get(0);
    }

    public boolean isProcessExist(String tenantId, String processName) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(tenantId, false,
                ProcessConstant.BuiltIn.NO, null, null, null, processName);

        return !processes.isEmpty();
    }

    /**
     * Get built-in process from database
     *
     * @param tenantId tenant id of the company
     * @param type     process type @see ProcessConstant.Type
     * @return built-in process
     */
    public OperatorProcess getBuiltIn(String tenantId, String type) {
        return get(tenantId, ProcessConstant.BuiltIn.YES, type);
    }

    /**
     * Get process which is template from database
     *
     * @param type process type @see ProcessConstant.Type
     * @return template process
     */
    public OperatorProcess getTemplate(String type) {
        List<OperatorProcess> processes = metadataProcessManager.getAll(null, false,
                ProcessConstant.BuiltIn.NO, ProcessConstant.IsTemplate.YES, null, type, null);
        if (processes == null || processes.isEmpty()) {
            return null;
        }
        log.info("Get process template named[%s] from database successfully.", processes.get(0).getName());

        return processes.get(0);
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
        OperatorProcess process = getFromCache(processId, false);
        OperatorMeta meta = operatorMetaMap.get(dto.getOperatorType());
        try {
            Operator operator = (Operator) meta.getType().getType().newInstance();
            operator.setName(meta.getName() + "-" + UUID.randomUUID().toString());
            operator.setType(dto.getOperatorType());
            operator.setxPos(dto.getxPos());
            operator.setyPos(dto.getyPos());
            operator.setFullName(dto.getFullName());
            process.getRootOperator().getExecutionUnit().addOperator(operator);
            process.setUpdateTime(new DateTime());
            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] add operator[name:%s] successfully.",
                    process.getName(), processId, meta.getName());

            return process;
        } catch (Throwable e) {
            log.error(e, "add process %s error", processId);
            throw new RuntimeException(e);
        }
    }

    public Operator getOperator(String processId, String operatorId) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            return operator;
        } else {
            return null;
        }
    }

    public OperatorProcess deleteOperator(String processId, String operatorId) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.removeOperator(operatorId);
            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] delete operator[id:%s] successfully.",
                    process.getName(), processId, operatorId);

            return process;
        } else {
            return null;
        }
    }

    public OperatorProcess connect(String processId, Connection dto) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.connect(dto, true);
            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] add connection successfully.",
                    process.getName(), processId);

            process.getRootOperator().getExecutionUnit().transformMetaData();
            return process;
        } else {
            return null;
        }
    }

    public OperatorProcess disconnect(String processId, Connection dto) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            process.setUpdateTime(new DateTime());
            process.disconnect(dto);
            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] delete connection successfully.",
                    process.getName(), processId);

            process.getRootOperator().getExecutionUnit().transformMetaData();
            return process;
        } else {
            return null;
        }
    }

    public Operator updateParameter(String processId, String operatorId, List<OperatorParamDto> paramList) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            if (!paramList.isEmpty()) {
                paramList.forEach(param -> {
                    operator.setParameter(param.getKey(), param.getValue());
                });

                process.getRootOperator().getExecutionUnit().transformMetaData();
            }
            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] update operator[%s] parameter[%s] successfully.",
                    process.getName(), processId, operator.getName(), paramList);

            return operator;
        } else {
            return null;
        }
    }

    public Operator updateOperator(String processId, String operatorId, OperatorDto dto) {
        OperatorProcess process = getFromCache(processId);
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
            if (dto.getOperatorType() != null) {
                operator.setType(dto.getOperatorType());
            }

            metadataProcessManager.update(process);

            log.info("The process named %s[id:%s] update operator[id:%s] successfully.",
                    process.getName(), processId, operatorId);

            return operator;
        } else {
            return null;
        }
    }

    public Operator cloneOperator(String processId, String operatorId, OperatorDto dto) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator newOperator = null;
            Operator operator = process.getOperator(operatorId);
            OperatorMeta meta = operatorMetaMap.get(operator.getType());

            // Deep clone operator
            try {
                newOperator = jsonMapper.readValue(
                        jsonMapper.writeValueAsBytes(operator),
                        new TypeReference<Operator>() {
                        }
                );
            } catch (IOException e) {
                log.error("Deep clone operator failed.", e);
            }

            if (newOperator != null) {
                newOperator.setName(meta.getName() + "-" + UUID.randomUUID().toString());
                newOperator.setxPos(dto.getxPos());
                newOperator.setyPos(dto.getyPos());
                newOperator.setFullName(operator.getFullName() + I18N.getMessage("pio.Operator.clone_suffix"));
                newOperator.setStatus(Status.INIT);
                process.getRootOperator().getExecutionUnit().addOperator(newOperator);
                process.setUpdateTime(new DateTime());

                metadataProcessManager.update(process);

                log.info("The process named %s[id:%s] clone operator[id:%s] successfully.",
                        process.getName(), processId, newOperator.getName());

                return newOperator;
            }
        }

        return null;
    }

    public Operator updateExampleData(String processId, String operatorId, List<String> dataList) {
        OperatorProcess process = getFromCache(processId);
        if (process != null && !Status.DELETED.equals(process.getStatus())) {
            Operator operator = process.getOperator(operatorId);
            if (!dataList.isEmpty()) {
                operator.getParameters().setExternalData(dataList);

                log.info("The process named %s[id:%s] update operator[%s] example data successfully.",
                        process.getName(), processId, operator.getName());
            }

            return operator;
        } else {
            return null;
        }
    }

    public OperatorProcess getResult(String id) {
        OperatorProcess process = getFromCache(id);
        IOContainer result = process.getRootOperator().getResults(true);

        log.info("Get result of process named %s[id:%s] successfully.",
                process.getName(), id);

        return process;
    }

    private OperatorProcess cloneProcess(OperatorProcess newProcess, OperatorProcess originProcess) {
        try {
            // Deep clone root operator
            ProcessRootOperator root = jsonMapper.readValue(
                    jsonMapper.writeValueAsBytes(originProcess.getRootOperator()),
                    new TypeReference<ProcessRootOperator>() {
                    }
            );

            /*root.getExecutionUnit().getOperators().forEach(operator -> {
                newProcess.registerName(operator.getName(), operator);
            });*/
//            root.setProcess(newProcess);
            newProcess.setRootOperator(root);
            /*newProcess.getRootOperator().setExecUnits(root.getExecUnits());
            newProcess.getRootOperator().setStatus(root.getStatus());
            root.getExecutionUnit().getOperators().forEach(operator -> {
                newProcess.registerName(operator.getName(), operator);
            });*/

            // Deep clone connections
            byte[] cBytes = jsonMapper.writeValueAsBytes(originProcess.getConnections());
            if (cBytes != null & cBytes.length > 0) {
                Set<Connection> connections = jsonMapper.readValue(cBytes,
                        new TypeReference<Set<Connection>>() {
                        }
                );
                newProcess.setConnections(connections);
            }
        } catch (IOException e) {
            log.error("Deep clone operators or connections failed.", e);
        }

        processCache.put(newProcess.getId(), newProcess);
        metadataProcessManager.insert(newProcess);

        log.info("Clone process['%s] from origin process[%s] successfully.", newProcess.getId(), originProcess.getName());

        return newProcess;
    }
}
