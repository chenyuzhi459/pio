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

    @JsonProperty("operator_processes")
    private String operatorProcessTable;

    @JsonProperty("recommend_instances")
    private String recommendInstanceTable;

    private final Map<String, String> entryTables = Maps.newHashMap();

    @JsonCreator
    public MetadataStorageTablesConfig(
        @JsonProperty("base") String base,
        @JsonProperty("config") String configTable,
        @JsonProperty("tasks") String tasksTable,
        @JsonProperty("operator_processes") String operatorProcessTable,
        @JsonProperty("recommend_instances") String recommendInstanceTable
    ) {
        this.base = (base == null) ? DEFAULT_BASE : base;
        this.operatorProcessTable = makeTableName(operatorProcessTable, "operator_processes");
        this.recommendInstanceTable = makeTableName(recommendInstanceTable, "recommend_instances");

        this.configTable = makeTableName(configTable, "config");

        this.tasksTable = makeTableName(tasksTable, "tasks");
        entryTables.put(TASK_ENTRY_TYPE, this.tasksTable);
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

    public String getRecommendInstanceTable() {
        return recommendInstanceTable;
    }

    public String getConfigTable()
    {
        return configTable;
    }

    public String getEntryTable(final String entryType)
    {
        return entryTables.get(entryType);
    }

    public String getTaskEntryType()
    {
        return TASK_ENTRY_TYPE;
    }
}
