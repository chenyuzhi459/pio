package io.sugo.pio.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class MetadataStorageTablesConfig {
    private static final String DEFAULT_BASE = "pio";

    @JsonProperty("base")
    private final String base;

    @JsonProperty("engines")
    private String engineTable;

    @JsonProperty("process_instance")
    private String processInstanceTable;

    @JsonCreator
    public MetadataStorageTablesConfig(
        @JsonProperty("base") String base,
        @JsonProperty("engines") String engineTable,
        @JsonProperty("process_instance") String processInstanceTable
    ) {
        this.base = (base == null) ? DEFAULT_BASE : base;
        this.engineTable = makeTableName(engineTable, "engines");;
        this.processInstanceTable = makeTableName(processInstanceTable, "process_instances");
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

    public String getEngineTable()
    {
        return engineTable;
    }

    public String getProcessInstanceTable() {
        return processInstanceTable;
    }

    @Override
    public String toString() {
        return String.format("%s{base=%s,engineTable=%s,processInstanceTable=%s}",
                MetadataStorageTablesConfig.class.getSimpleName(),
                base, engineTable, processInstanceTable);
    }
}
