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

    @JsonProperty("operator_processes")
    private String operatorProcessTable;

    @JsonCreator
    public MetadataStorageTablesConfig(
        @JsonProperty("base") String base,
        @JsonProperty("engines") String engineTable,
        @JsonProperty("operator_processes") String operatorProcessTable
    ) {
        this.base = (base == null) ? DEFAULT_BASE : base;
        this.engineTable = makeTableName(engineTable, "engines");;
        this.operatorProcessTable = makeTableName(operatorProcessTable, "operator_processes");
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

    public String getOperatorProcessTable() {
        return operatorProcessTable;
    }

    @Override
    public String toString() {
        return String.format("%s{base=%s,engineTable=%s,operatorProcessTable=%s}",
                MetadataStorageTablesConfig.class.getSimpleName(),
                base, engineTable, operatorProcessTable);
    }
}
