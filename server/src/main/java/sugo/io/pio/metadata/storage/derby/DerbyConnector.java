package sugo.io.pio.metadata.storage.derby;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.metamx.common.logger.Logger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import sugo.io.pio.metadata.MetadataStorageConnectorConfig;
import sugo.io.pio.metadata.MetadataStorageTablesConfig;
import sugo.io.pio.metadata.SQLMetadataConnector;

/**
 */
public class DerbyConnector extends SQLMetadataConnector {
    private static final Logger log = new Logger(DerbyConnector.class);
    private final DBI dbi;

    @Inject
    public DerbyConnector(Supplier<MetadataStorageConnectorConfig> config, Supplier<MetadataStorageTablesConfig> dbTables)
    {
        super(config, dbTables);

        final BasicDataSource datasource = getDatasource();
        datasource.setDriverClassLoader(getClass().getClassLoader());
        datasource.setDriverClassName("org.apache.derby.jdbc.ClientDriver");

        this.dbi = new DBI(datasource);

        log.info("Configured Derby as metadata storage");
    }

    @Override
    public boolean tableExists(Handle handle, String tableName)
    {
        return !handle.createQuery("select * from SYS.SYSTABLES where tablename = :tableName")
                .bind("tableName", tableName.toUpperCase())
                .list()
                .isEmpty();
    }


    @Override
    public DBI getDBI() {
        return dbi;
    }

    @Override
    public String getValidationQuery() { return "VALUES 1"; }
}
