package io.sugo.pio.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.server.process.ProcessInstance;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.tweak.HandleCallback;

@ManageLifecycle
public class SQLMetadataProcessInstanceManager implements MetadataProcessInstanceManager {
    private static final Logger log = new Logger(SQLMetadataProcessInstanceManager.class);

    private final Object lock = new Object();

    private final ObjectMapper jsonMapper;
    private final Supplier<MetadataStorageTablesConfig> dbTables;
    private final SQLMetadataConnector connector;

    private final IDBI dbi;

    @Inject
    public SQLMetadataProcessInstanceManager(
            ObjectMapper jsonMapper,
            Supplier<MetadataStorageTablesConfig> dbTables,
            SQLMetadataConnector connector
    ) {
        this.jsonMapper = jsonMapper;
        this.dbTables = dbTables;
        this.connector = connector;
        this.dbi = connector.getDBI();
    }

    @LifecycleStart
    public void start() {
        connector.createProcessInstanceTable();
    }

    @LifecycleStop
    public void stop() {
    }

    @Override
    public void insert(final ProcessInstance pi) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format(
                                            "INSERT INTO %s (id, process_name, status, created_date, process) " +
                                                    " VALUES (:id, :process_name, :status, :created_date, :process)",
                                            getProcessInstanceTable()
                                    )
                            )
                                    .bind("id", pi.getId())
                                    .bind("process_name", pi.getProcessName())
                                    .bind("status", pi.getStatus().name())
                                    .bind("created_date", new DateTime().toString())
                                    .bind("process", jsonMapper.writeValueAsBytes(pi.getProcess()))
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception insert process %s[%s]", pi.getProcessName(), pi.getId());
        }
    }

    @Override
    public boolean updateStatus(final ProcessInstance pi) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format("UPDATE %s SET status = :status, update_date = :update_date WHERE id = :id",
                                            getProcessInstanceTable())
                            )
                                    .bind("id", pi.getId())
                                    .bind("status", pi.getStatus().name())
                                    .bind("update_date", new DateTime().toString())
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception update process %s[%s] status %s to %s", pi.getProcessName(), pi.getId(), pi.getStatus(), pi.getPreStatus());
            return false;
        }
        return true;
    }

    private String getProcessInstanceTable() {
        return dbTables.get().getProcessInstanceTable();
    }
}
