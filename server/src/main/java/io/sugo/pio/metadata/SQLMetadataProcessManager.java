package io.sugo.pio.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@ManageLifecycle
public class SQLMetadataProcessManager implements MetadataProcessManager {
    private static final Logger log = new Logger(SQLMetadataProcessManager.class);

    private final ObjectMapper jsonMapper;
    private final Supplier<MetadataStorageTablesConfig> dbTables;
    private final SQLMetadataConnector connector;

    private final IDBI dbi;

    @Inject
    public SQLMetadataProcessManager(
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
        connector.createOperatorProcessTable();
    }

    @LifecycleStop
    public void stop() {
    }

    @Override
    public OperatorProcess get(String id) {
        return dbi.withHandle(
                new HandleCallback<OperatorProcess>() {
                    @Override
                    public OperatorProcess withHandle(Handle handle) throws Exception {

                        return handle.createQuery(String.format(
                                "SELECT id, name, status, created_date, update_date, operators FROM %1$s where id = :id ORDER BY id DESC LIMIT 1 OFFSET 0",
                                getOperatorProcessTable()

                        )).bind("id", id).map(new ResultSetMapper<OperatorProcess>() {

                            @Override
                            public OperatorProcess map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                try {
                                    String id = r.getString("id");
                                    String name = r.getString("name");
                                    Status status = Status.valueOf(r.getString("status"));
                                    DateTime createTime = new DateTime(r.getString("created_date"));
                                    DateTime updateTime = new DateTime(r.getString("update_date"));
                                    ProcessRootOperator root = jsonMapper.readValue(
                                            r.getBytes("operators"), new TypeReference<ProcessRootOperator>() {
                                            });
                                    OperatorProcess process = new OperatorProcess(name, root);
                                    process.setId(id);
//                                    process.setName(name);
                                    process.setStatus(status);
                                    process.setCreateTime(createTime);
                                    process.setUpdateTime(updateTime);
//                                    process.setRootOperator(root);
                                    return process;
                                } catch (IOException e) {
                                    throw Throwables.propagate(e);
                                }
                            }
                        }).first();
                    }
                });
    }

    @Override
    public void insert(final OperatorProcess process) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format(
                                            "INSERT INTO %s (id, name, status, created_date, update_date, operators) " +
                                                    " VALUES (:id, :name, :status, :created_date, :update_date, :operators)",
                                            getOperatorProcessTable()
                                    )
                            )
                                    .bind("id", process.getId())
                                    .bind("name", process.getName())
                                    .bind("status", process.getStatus().name())
                                    .bind("created_date", process.getCreateTime())
                                    .bind("update_date", process.getUpdateTime())
                                    .bind("operators", jsonMapper.writeValueAsBytes(process.getRootOperator()))
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception insert process %s[%s]", process.getName(), process.getId());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateStatus(final OperatorProcess process) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format("UPDATE %s SET status = :status, update_date = :update_date, operators = :operators WHERE id = :id",
                                            getOperatorProcessTable())
                            )
                                    .bind("id", process.getId())
                                    .bind("status", process.getStatus().name())
                                    .bind("update_date", process.getUpdateTime().toString())
                                    .bind("operators", jsonMapper.writeValueAsBytes(process.getRootOperator()))
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception update process [%s] status %s", process.getId(), process.getStatus());
            return false;
        }
        return true;
    }

    private String getOperatorProcessTable() {
        return dbTables.get().getOperatorProcessTable();
    }
}
