package io.sugo.pio.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.OperatorProcess;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.jackson.DefaultObjectMapper;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.operator.Status;
import io.sugo.pio.ports.Connection;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        this.jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
        return get(id, true);
    }

    @Override
    public OperatorProcess get(String id, boolean includeDelete) {
        return dbi.withHandle(
                new HandleCallback<OperatorProcess>() {
                    @Override
                    public OperatorProcess withHandle(Handle handle) throws Exception {

                        StringBuilder builder = new StringBuilder();
                        builder.append("SELECT id, name, description, status, created_date, update_date, operators, connections ")
                                .append(" FROM %1$s ")
                                .append(" WHERE id = :id ");
                        if (!includeDelete) {
                            builder.append(" AND status != :status");
                        }
                        builder.append(" ORDER BY id DESC LIMIT 1 OFFSET 0");
                        Query<Map<String, Object>> query = handle.createQuery(
                                String.format(builder.toString(), getTableName())
                        ).bind("id", id);
                        if (!includeDelete) {
                            query.bind("status", Status.DELETED);
                        }

                        return query.map(new ResultSetMapper<OperatorProcess>() {

                            @Override
                            public OperatorProcess map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                try {
                                    ProcessRootOperator root = jsonMapper.readValue(
                                            r.getBytes("operators"), new TypeReference<ProcessRootOperator>() {
                                            });

                                    OperatorProcess process = new OperatorProcess(r.getString("name"), root);
                                    process.setId(r.getString("id"));
                                    byte[] cBytes = r.getBytes("connections");
                                    if (cBytes != null && cBytes.length > 0) {
                                        Set<Connection> connections = jsonMapper.readValue(cBytes,
                                                new TypeReference<Set<Connection>>() {
                                                }
                                        );
                                        process.setConnections(connections);
                                    }
                                    process.setDescription(r.getString("description"));
                                    process.setStatus(Status.valueOf(r.getString("status")));
                                    process.setCreateTime(new DateTime(r.getString("created_date")));
                                    process.setUpdateTime(new DateTime(r.getString("update_date")));

                                    // when a single process first loaded, then transform the metadata
                                    process.getRootOperator().getExecutionUnit().transformMetaData();

                                    log.info("Get process named %s[id:%s] from database successfully.", process.getName(), id);

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
    public List<OperatorProcess> getAll(boolean includeDelete) {
        return dbi.withHandle(
                new HandleCallback<List<OperatorProcess>>() {
                    @Override
                    public List<OperatorProcess> withHandle(Handle handle) throws Exception {

                        StringBuilder builder = new StringBuilder();
                        builder.append("SELECT id, name, description, status, created_date, update_date, operators, connections ")
                                .append(" FROM %1$s ")
                                .append(" WHERE 1 = 1 ");
                        if (!includeDelete) {
                            builder.append(" AND status != :status");
                        }
                        builder.append(" ORDER BY created_date DESC");
                        Query<Map<String, Object>> query = handle.createQuery(
                                String.format(builder.toString(), getTableName())
                        );
                        if (!includeDelete) {
                            query.bind("status", Status.DELETED);
                        }

                        return query.map(new ResultSetMapper<OperatorProcess>() {
                            @Override
                            public OperatorProcess map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                try {
                                    ProcessRootOperator root = jsonMapper.readValue(
                                            r.getBytes("operators"), new TypeReference<ProcessRootOperator>() {
                                            });
                                    OperatorProcess process = new OperatorProcess(r.getString("name"), root);
                                    process.setId(r.getString("id"));
                                    byte[] cBytes = r.getBytes("connections");
                                    if (cBytes != null & cBytes.length > 0) {
                                        Set<Connection> connections = jsonMapper.readValue(cBytes,
                                                new TypeReference<Set<Connection>>() {
                                                }
                                        );
                                        process.setConnections(connections);
                                    }
                                    process.setDescription(r.getString("description"));
                                    process.setStatus(Status.valueOf(r.getString("status")));
                                    process.setCreateTime(new DateTime(r.getString("created_date")));
                                    process.setUpdateTime(new DateTime(r.getString("update_date")));
                                    return process;
                                } catch (IOException e) {
                                    log.error("Deserialized process error: %s", e);
                                    throw Throwables.propagate(e);
                                }
                            }
                        }).fold(Lists.newArrayList(), new Folder3<ArrayList<OperatorProcess>, OperatorProcess>() {
                            @Override
                            public ArrayList<OperatorProcess> fold(
                                    ArrayList<OperatorProcess> retVal,
                                    OperatorProcess rs,
                                    FoldController control,
                                    StatementContext ctx
                            ) throws SQLException {
                                try {
                                    retVal.add(rs);
                                    return retVal;
                                } catch (Exception e) {
                                    throw Throwables.propagate(e);
                                }
                            }
                        });

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
                                            "INSERT INTO %s (id, name, description, status, created_date, update_date, operators, connections) " +
                                                    " VALUES (:id, :name, :description, :status, :created_date, :update_date, :operators, :connections)",
                                            getTableName()
                                    )
                            )
                                    .bind("id", process.getId())
                                    .bind("name", process.getName())
                                    .bind("description", process.getDescription())
                                    .bind("status", process.getStatus().name())
                                    .bind("created_date", process.getCreateTime())
                                    .bind("update_date", process.getUpdateTime())
                                    .bind("operators", jsonMapper.writeValueAsBytes(process.getRootOperator()))
                                    .bind("connections", jsonMapper.writeValueAsBytes(process.getConnections()))
                                    .execute();

                            log.info("Insert process named %s[id:%s] to database successfully.", process.getName(), process.getId());

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
    public boolean update(final OperatorProcess process) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format("UPDATE %s SET name=:name, description=:description, status=:status, update_date=:update_date, operators = :operators, connections=:connections WHERE id = :id",
                                            getTableName())
                            )
                                    .bind("id", process.getId())
                                    .bind("name", process.getName())
                                    .bind("description", process.getDescription())
                                    .bind("status", process.getStatus().name())
                                    .bind("update_date", process.getUpdateTime().toString())
                                    .bind("operators", jsonMapper.writeValueAsBytes(process.getRootOperator()))
                                    .bind("connections", jsonMapper.writeValueAsBytes(process.getConnections()))
                                    .execute();

                            log.info("Update process named %s[id:%s] to database successfully.", process.getName(), process.getId());

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

    private String getTableName() {
        return dbTables.get().getOperatorProcessTable();
    }
}
