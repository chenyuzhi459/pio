package io.sugo.pio.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 */
public class MetadataStorageTablesConfig {
    private static final String DEFAULT_BASE = "pio";

    public static final String TASK_ENTRY_TYPE = "task";

    @JsonProperty("base")
    private final String base;

    @JsonProperty("config")
    private final String configTable;

    @JsonProperty("tasks")
    private final String tasksTable;

    @JsonProperty("taskLog")
    private final String taskLogTable;

    @JsonProperty("taskLock")
    private final String taskLockTable;

    @JsonProperty("operator_processes")
    private String operatorProcessTable;

    private final Map<String, String> entryTables = Maps.newHashMap();
    private final Map<String, String> logTables = Maps.newHashMap();
    private final Map<String, String> lockTables = Maps.newHashMap();

    @JsonCreator
    public MetadataStorageTablesConfig(
        @JsonProperty("base") String base,
        @JsonProperty("config") String configTable,
        @JsonProperty("tasks") String tasksTable,
        @JsonProperty("taskLog") String taskLogTable,
        @JsonProperty("taskLock") String taskLockTable,
        @JsonProperty("operator_processes") String operatorProcessTable
    ) {
        this.base = (base == null) ? DEFAULT_BASE : base;
        this.operatorProcessTable = makeTableName(operatorProcessTable, "operator_processes");

        this.configTable = makeTableName(configTable, "config");

        this.tasksTable = makeTableName(tasksTable, "tasks");
        this.taskLogTable = makeTableName(taskLogTable, "tasklogs");
        this.taskLockTable = makeTableName(taskLockTable, "tasklocks");
        entryTables.put(TASK_ENTRY_TYPE, this.tasksTable);
        logTables.put(TASK_ENTRY_TYPE, this.taskLogTable);
        lockTables.put(TASK_ENTRY_TYPE, this.taskLockTable);
    }

    private String makeTableName(String explicitTableName, String defaultSuffix)
    {
        if (explicitTableName == null) {
            if (base == null) {
                return null;
            }
            return String.format("%s_%s", base, defaultSuffix);
        }

        return explicitTableName;
    }

    public String getOperatorProcessTable() {
        return operatorProcessTable;
    }

    public String getConfigTable()
    {
        return configTable;
    }

    public String getEntryTable(final String entryType)
    {
        return entryTables.get(entryType);
    }

    public String getLogTable(final String entryType)
    {
        return logTables.get(entryType);
    }

    public String getLockTable(final String entryType)
    {
        return lockTables.get(entryType);
    }

    public String getTaskEntryType()
    {
        return TASK_ENTRY_TYPE;
    }
}
