package io.sugo.pio.metadata;

/**
 */
public interface MetadataStorageConnector {
    Void insertOrUpdate(
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key,
            final byte[] value
    ) throws Exception;

    byte[] lookup(
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key
    );

    void createOperatorProcessTable();

    void createTaskTables();

    void createConfigTable();
}
