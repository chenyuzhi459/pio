package io.sugo.pio.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.metamx.common.Pair;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.Process;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.Status;
import io.sugo.pio.server.process.ProcessInstance;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
            @Json ObjectMapper jsonMapper,
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
    public ProcessInstance get(String id) {
        return dbi.withHandle(
                new HandleCallback<ProcessInstance>() {
                    @Override
                    public ProcessInstance withHandle(Handle handle) throws Exception {

                        return handle.createQuery(String.format(
                                "SELECT id, process_name, status, created_date, update_date, process FROM %1$s where id = :id ORDER BY id DESC LIMIT 1 OFFSET 0",
                                getProcessInstanceTable()

                        )).bind("id", id).map(new ResultSetMapper<ProcessInstance>() {

                            @Override
                            public ProcessInstance map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                ProcessInstance pi = null;
                                try {
                                    Process process = jsonMapper.readValue(
                                            r.getBytes("process"),
                                            new TypeReference<Process>() {
                                            });
                                    Status status = Status.valueOf(r.getString("status"));
                                    DateTime createTime = new DateTime(r.getString("created_date"));
                                    DateTime updateTime = new DateTime(r.getString("update_date"));
                                    pi = new ProcessInstance(process, status, createTime, updateTime);
                                } catch (IOException e) {
                                    throw Throwables.propagate(e);
                                }
                                return pi;
                            }
                        }).first();
                    }
                });
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
                                            "INSERT INTO %s (id, process_name, status, created_date, update_date, process) " +
                                                    " VALUES (:id, :process_name, :status, :created_date, :update_date, :process)",
                                            getProcessInstanceTable()
                                    )
                            )
                                    .bind("id", pi.getId())
                                    .bind("process_name", pi.getProcessName())
                                    .bind("status", pi.getStatus().name())
                                    .bind("created_date", new DateTime().toString())
                                    .bind("update_date", new DateTime().toString())
                                    .bind("process", jsonMapper.writeValueAsBytes(pi.getProcess()))
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception insert process %s[%s]", pi.getProcessName(), pi.getId());
            throw new RuntimeException(e);
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
                                    String.format("UPDATE %s SET status = :status, update_date = :update_date, process = :process WHERE id = :id",
                                            getProcessInstanceTable())
                            )
                                    .bind("id", pi.getId())
                                    .bind("status", pi.getStatus().name())
                                    .bind("update_date", new DateTime().toString())
                                    .bind("process", jsonMapper.writeValueAsBytes(pi.getProcess()))
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
