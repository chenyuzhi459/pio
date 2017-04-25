package io.sugo.pio.metadata;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.metamx.common.ISE;
import com.metamx.common.RetryUtils;
import com.metamx.common.logger.Logger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.util.ByteArrayMapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 */
public abstract class SQLMetadataConnector implements MetadataStorageConnector {
    private static final Logger log = new Logger(SQLMetadataConnector.class);
    private static final String PAYLOAD_TYPE = "BLOB";

    public static final int DEFAULT_MAX_TRIES = 3;

    private final Supplier<MetadataStorageConnectorConfig> config;
    private final Supplier<MetadataStorageTablesConfig> tablesConfigSupplier;
    private final Predicate<Throwable> shouldRetry;

    public SQLMetadataConnector(
            Supplier<MetadataStorageConnectorConfig> config,
            Supplier<MetadataStorageTablesConfig> tablesConfigSupplier) {
        this.config = config;
        this.tablesConfigSupplier = tablesConfigSupplier;
        this.shouldRetry = new Predicate<Throwable>() {
            @Override
            public boolean apply(Throwable e) {
                return isTransientException(e);
            }
        };
    }

    /**
     * SQL type to use for payload data (e.g. JSON blobs).
     * Must be a binary type, which values can be accessed using ResultSet.getBytes()
     * <p/>
     * The resulting string will be interpolated into the table creation statement, e.g.
     * <code>CREATE TABLE druid_table ( payload <type> NOT NULL, ... )</code>
     *
     * @return String representing the SQL type
     */
    protected String getPayloadType() {
        return PAYLOAD_TYPE;
    }

    /**
     * Auto-incrementing SQL type to use for IDs
     * Must be an integer type, which values will be automatically set by the database
     * <p/>
     * The resulting string will be interpolated into the table creation statement, e.g.
     * <code>CREATE TABLE druid_table ( id <type> NOT NULL, ... )</code>
     *
     * @return String representing the SQL type and auto-increment statement
     */
    protected abstract String getSerialType();

    public abstract boolean tableExists(Handle handle, final String tableName);

    public String getValidationQuery() {
        return "SELECT 1";
    }

    public <T> T retryWithHandle(
            final HandleCallback<T> callback,
            final Predicate<Throwable> myShouldRetry
    ) {
        final Callable<T> call = new Callable<T>() {
            @Override
            public T call() throws Exception {
                return getDBI().withHandle(callback);
            }
        };
        try {
            return RetryUtils.retry(call, myShouldRetry, DEFAULT_MAX_TRIES);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public <T> T retryWithHandle(final HandleCallback<T> callback) {
        return retryWithHandle(callback, shouldRetry);
    }

    public <T> T retryTransaction(final TransactionCallback<T> callback, final int quietTries, final int maxTries) {
        final Callable<T> call = new Callable<T>() {
            @Override
            public T call() throws Exception {
                return getDBI().inTransaction(callback);
            }
        };
        try {
            return RetryUtils.retry(call, shouldRetry, quietTries, maxTries);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public final boolean isTransientException(Throwable e) {
        return e != null && (e instanceof SQLTransientException
                || e instanceof SQLRecoverableException
                || e instanceof UnableToObtainConnectionException
                || e instanceof UnableToExecuteStatementException
                || connectorIsTransientException(e)
                || (e instanceof SQLException && isTransientException(e.getCause()))
                || (e instanceof DBIException && isTransientException(e.getCause())));
    }

    protected boolean connectorIsTransientException(Throwable e) {
        return false;
    }

    public void createTable(final String tableName, final Iterable<String> sql) {
        try {
            retryWithHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            if (!tableExists(handle, tableName)) {
                                log.info("Creating table[%s]", tableName);
                                final Batch batch = handle.createBatch();
                                for (String s : sql) {
                                    batch.add(s);
                                }
                                batch.execute();
                            } else {
                                log.info("Table[%s] already exists", tableName);
                            }
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.warn(e, "Exception creating table");
        }
    }


    public void createConfigTable(final String tableName) {
        createTable(
                tableName,
                ImmutableList.of(
                        String.format(
                                "CREATE TABLE %1$s (\n"
                                        + "  name VARCHAR(255) NOT NULL,\n"
                                        + "  payload %2$s NOT NULL,\n"
                                        + "  PRIMARY KEY(name)\n"
                                        + ")",
                                tableName, getPayloadType()
                        )
                )
        );
    }

    public void createEntryTable(final String tableName) {
        createTable(
                tableName,
                ImmutableList.of(
                        String.format(
                                "CREATE TABLE %1$s (\n"
                                        + "  id VARCHAR(255) NOT NULL,\n"
                                        + "  created_date VARCHAR(255) NOT NULL,\n"
                                        + "  payload %2$s NOT NULL,\n"
                                        + "  status_payload %2$s NOT NULL,\n"
                                        + "  active BOOLEAN NOT NULL DEFAULT FALSE,\n"
                                        + "  PRIMARY KEY (id)\n"
                                        + ")",
                                tableName, getPayloadType()
                        ),
                        String.format("CREATE INDEX idx_%1$s_active_created_date ON %1$s(active, created_date)", tableName)
                )
        );
    }

    @Override
    public Void insertOrUpdate(
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key,
            final byte[] value
    ) throws Exception {
        return getDBI().inTransaction(
                new TransactionCallback<Void>() {
                    @Override
                    public Void inTransaction(Handle handle, TransactionStatus transactionStatus) throws Exception {
                        int count = handle
                                .createQuery(
                                        String.format("SELECT COUNT(*) FROM %1$s WHERE %2$s = :key", tableName, keyColumn)
                                )
                                .bind("key", key)
                                .map(IntegerMapper.FIRST)
                                .first();
                        if (count == 0) {
                            handle.createStatement(
                                    String.format(
                                            "INSERT INTO %1$s (%2$s, %3$s) VALUES (:key, :value)",
                                            tableName, keyColumn, valueColumn
                                    )
                            )
                                    .bind("key", key)
                                    .bind("value", value)
                                    .execute();
                        } else {
                            handle.createStatement(
                                    String.format(
                                            "UPDATE %1$s SET %3$s=:value WHERE %2$s=:key",
                                            tableName, keyColumn, valueColumn
                                    )
                            )
                                    .bind("key", key)
                                    .bind("value", value)
                                    .execute();
                        }
                        return null;
                    }
                }
        );
    }

    public abstract DBI getDBI();

    public MetadataStorageConnectorConfig getConfig() {
        return config.get();
    }

    protected BasicDataSource getDatasource() {
        MetadataStorageConnectorConfig connectorConfig = getConfig();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername(connectorConfig.getUser());
        dataSource.setPassword(connectorConfig.getPassword());
        String uri = connectorConfig.getConnectURI();
        dataSource.setUrl(uri);

        dataSource.setValidationQuery(getValidationQuery());
        dataSource.setTestOnBorrow(true);

        return dataSource;
    }

    public void createOperatorProcessTable(final String tableName) {
        createTable(
                tableName,
                ImmutableList.of(
                        String.format(
                                "CREATE TABLE %1$s (\n"
                                        + "  id VARCHAR(255) NOT NULL,\n"
                                        + "  name VARCHAR(50) NOT NULL,\n"
                                        + "  description VARCHAR(250),\n"
                                        + "  status VARCHAR(20) NOT NULL,\n"
                                        + "  created_date VARCHAR(50) NOT NULL,\n"
                                        + "  update_date VARCHAR(50) NOT NULL,\n"
                                        + "  operators %2$s,\n"
                                        + "  connections %3$s,\n"
                                        + "  tenant_id VARCHAR(255) NOT NULL,\n"
                                        + "  built_in int DEFAULT 0 NOT NULL,\n"
                                        + "  is_template int DEFAULT 0 NOT NULL,\n"
                                        + "  type VARCHAR(50),\n"
                                        + "  is_case int DEFAULT 0 NOT NULL,\n"
                                        + "  PRIMARY KEY (id)\n"
                                        + ")",
                                tableName, getPayloadType(), getPayloadType()
                        )
                )
        );
    }

    @Override
    public void createOperatorProcessTable() {
        if (config.get().isCreateTables()) {
            createOperatorProcessTable(tablesConfigSupplier.get().getOperatorProcessTable());
        }
    }

    public void createRecommendInstanceTable(final String tableName) {
        createTable(
                tableName,
                ImmutableList.of(
                        String.format(
                                "CREATE TABLE %1$s (\n"
                                        + "  id VARCHAR(50) NOT NULL,\n"
                                        + "  name VARCHAR(50) NOT NULL,\n"
                                        + "  num int4,\n"
                                        + "  owner VARCHAR(50),\n"
                                        + "  create_date timestamp(0) without time zone,\n"
                                        + "  update_date timestamp(0) without time zone,\n"
                                        + "  enabled boolean,\n"
                                        + "  strategies %2$s,\n"
                                        + "  PRIMARY KEY (id)\n"
                                        + ")",
                                tableName, getPayloadType()
                        )
                )
        );
    }

    @Override
    public void createRecommendInstanceTable() {
        if (config.get().isCreateTables()) {
            createRecommendInstanceTable(tablesConfigSupplier.get().getRecommendInstanceTable());
        }
    }

    @Override
    public void createTaskTables() {
        if (config.get().isCreateTables()) {
            final MetadataStorageTablesConfig tablesConfig = tablesConfigSupplier.get();
            final String entryType = tablesConfig.getTaskEntryType();
            createEntryTable(tablesConfig.getEntryTable(entryType));
        }
    }

    @Override
    public void createConfigTable() {
        if (config.get().isCreateTables()) {
            createConfigTable(tablesConfigSupplier.get().getConfigTable());
        }
    }

    @Override
    public byte[] lookup(
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key
    ) {
        return getDBI().withHandle(
                new HandleCallback<byte[]>() {
                    @Override
                    public byte[] withHandle(Handle handle) throws Exception {
                        return lookupWithHandle(handle, tableName, keyColumn, valueColumn, key);
                    }
                }
        );
    }

    public byte[] lookupWithHandle(
            final Handle handle,
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key
    ) {
        final String selectStatement = String.format(
                "SELECT %s FROM %s WHERE %s = :key", valueColumn,
                tableName, keyColumn
        );

        List<byte[]> matched = handle.createQuery(selectStatement)
                .bind("key", key)
                .map(ByteArrayMapper.FIRST)
                .list();

        if (matched.isEmpty()) {
            return null;
        }

        if (matched.size() > 1) {
            throw new ISE("Error! More than one matching entry[%d] found for [%s]?!", matched.size(), key);
        }

        return matched.get(0);
    }

}
