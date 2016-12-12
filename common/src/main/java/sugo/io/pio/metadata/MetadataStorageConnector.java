package sugo.io.pio.metadata;

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
}
