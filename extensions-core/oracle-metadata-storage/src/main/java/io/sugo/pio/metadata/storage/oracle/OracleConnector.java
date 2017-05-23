package io.sugo.pio.metadata.storage.oracle;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.metamx.common.ISE;
import com.metamx.common.logger.Logger;
import io.sugo.pio.metadata.MetadataStorageConnectorConfig;
import io.sugo.pio.metadata.MetadataStorageTablesConfig;
import io.sugo.pio.metadata.SQLMetadataConnector;
import oracle.jdbc.driver.OracleSQLException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.util.BooleanMapper;

import java.sql.SQLException;

/**
 */
public class OracleConnector extends SQLMetadataConnector {
    private static final Logger log = new Logger(OracleConnector.class);
    private static final String PAYLOAD_TYPE = "LONGBLOB";
    private static final String SERIAL_TYPE = "BIGINT(20) AUTO_INCREMENT";

    private final DBI dbi;

    @Inject
    public OracleConnector(Supplier<MetadataStorageConnectorConfig> config, Supplier<MetadataStorageTablesConfig> dbTables) {
        super(config, dbTables);

        final BasicDataSource datasource = getDatasource();
        // Oracle driver is classloader isolated as part of the extension
        // so we need to help JDBC find the driver
        datasource.setDriverClassLoader(getClass().getClassLoader());
        datasource.setDriverClassName("oracle.jdbc.driver.OracleDriver");

        // use double-quotes for quoting columns, so we can write SQL that works with most databases
        datasource.setConnectionInitSqls(ImmutableList.of("SET sql_mode='ANSI_QUOTES'"));

        this.dbi = new DBI(datasource);

        log.info("Configured Oracle as metadata storage");
    }

    @Override
    protected String getPayloadType() {
        return PAYLOAD_TYPE;
    }

    @Override
    protected String getSerialType() {
        return SERIAL_TYPE;
    }


    @Override
    public boolean tableExists(Handle handle, String tableName) {
        // ensure database defaults to utf8, otherwise bail
        boolean isUtf8 = handle
                .createQuery("SELECT @@character_set_database = 'utf8'")
                .map(BooleanMapper.FIRST)
                .first();

        if (!isUtf8) {
            throw new ISE(
                    "Database default character set is not UTF-8." + System.lineSeparator()
                            + "  Pio requires its Oracle database to be created using UTF-8 as default character set."
            );
        }

        return !handle.createQuery("SHOW tables LIKE :tableName")
                .bind("tableName", tableName)
                .list()
                .isEmpty();
    }

    @Override
    protected boolean connectorIsTransientException(Throwable e) {
        return e instanceof OracleSQLException
                || (e instanceof SQLException && ((SQLException) e).getErrorCode() == 1317 /* ER_QUERY_INTERRUPTED */);
    }

    @Override
    public Void insertOrUpdate(
            final String tableName,
            final String keyColumn,
            final String valueColumn,
            final String key,
            final byte[] value
    ) throws Exception {
        return getDBI().withHandle(
                new HandleCallback<Void>() {
                    @Override
                    public Void withHandle(Handle handle) throws Exception {
                        handle.createStatement(
                                String.format(
                                        "INSERT INTO %1$s (%2$s, %3$s) VALUES (:key, :value) ON DUPLICATE KEY UPDATE %3$s = :value",
                                        tableName,
                                        keyColumn,
                                        valueColumn
                                )
                        )
                                .bind("key", key)
                                .bind("value", value)
                                .execute();
                        return null;
                    }
                }
        );
    }

    @Override
    public DBI getDBI() {
        return dbi;
    }
}

