package io.sugo.pio.metadata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.guice.annotations.Json;
import io.sugo.pio.recommend.bean.PageInfo;
import io.sugo.pio.recommend.bean.RecInstance;
import io.sugo.pio.recommend.bean.RecInstanceCriteria;
import io.sugo.pio.recommend.bean.RecStrategy;
import io.sugo.pio.server.utils.StringUtil;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManageLifecycle
public class SQLMetadataRecInstanceManager implements MetadataRecInstanceManager {
    private static final Logger log = new Logger(SQLMetadataRecInstanceManager.class);

    private final ObjectMapper jsonMapper;
    private final Supplier<MetadataStorageTablesConfig> dbTables;
    private final SQLMetadataConnector connector;

    private final IDBI dbi;

    private final static String ALL_FIELDS = "id, name, num, owner, create_date, update_date, enabled, strategies";

    @Inject
    public SQLMetadataRecInstanceManager(
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
        connector.createRecommendInstanceTable();
    }

    @LifecycleStop
    public void stop() {
    }

    @Override
    public RecInstance get(String id) {
        return dbi.withHandle(
                new HandleCallback<RecInstance>() {
                    @Override
                    public RecInstance withHandle(Handle handle) throws Exception {

                        StringBuilder builder = new StringBuilder();
                        builder.append("SELECT " + ALL_FIELDS)
                                .append(" FROM %1$s ")
                                .append(" WHERE id = :id ");
                        builder.append(" ORDER BY id DESC LIMIT 1 OFFSET 0");
                        Query<Map<String, Object>> query = handle.createQuery(
                                String.format(builder.toString(), getTableName())
                        ).bind("id", id);

                        return query.map(new ResultSetMapper<RecInstance>() {

                            @Override
                            public RecInstance map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                try {
                                    RecInstance entry = new RecInstance();
                                    entry.setId(r.getString("id"));
                                    entry.setName(r.getString("name"));
                                    entry.setNum(r.getInt("num"));
                                    entry.setOwner(r.getString("owner"));
                                    entry.setCreateTime(new DateTime(r.getTimestamp("create_date")));
                                    entry.setUpdateTime(new DateTime(r.getTimestamp("update_date")));
                                    entry.setEnabled(r.getBoolean("enabled"));
                                    byte[] cBytes = r.getBytes("strategies");
                                    Map<String, RecStrategy> strategies = jsonMapper.readValue(cBytes,
                                            new TypeReference<Map<String, RecStrategy>>() {
                                            });
                                    entry.setRecStrategys(strategies);
                                    return entry;
                                } catch (IOException e) {
                                    throw Throwables.propagate(e);
                                }
                            }
                        }).first();
                    }
                });
    }

    @Override
    public List<RecInstance> getAll(RecInstanceCriteria criteria) {
        return dbi.withHandle(
                new HandleCallback<List<RecInstance>>() {
                    @Override
                    public List<RecInstance> withHandle(Handle handle) throws Exception {

                        StringBuilder builder = new StringBuilder();
                        builder.append("SELECT id, name, num, owner, create_date, update_date, enabled ")
                                .append(" FROM %1$s ")
                                .append(" WHERE 1 = 1 ");
                        if (StringUtil.isNotEmpty(criteria.getName())) {
                            builder.append(" AND name like :name");
                        }
                        if (StringUtil.isNotEmpty(criteria.getOwner())) {
                            builder.append(" AND owner like :owner");
                        }
                        if (criteria.getEnabled() != null) {
                            builder.append(" AND enabled=:enabled");
                        }
                        if (criteria.getCreateTimeStart() != null) {
                            builder.append(" AND create_date>=:create_date_start");
                        }
                        if (criteria.getCreateTimeEnd() != null) {
                            builder.append(" AND create_date<=:create_date_end");
                        }
                        builder.append(" ORDER BY create_date DESC");
                        PageInfo page = criteria.getPageInfo();
                        if (page != null) {
                            builder.append(String.format(" LIMIT %d OFFSET %d", page.getPageSize(), page.getPageOffset()));
                        }

                        Query<Map<String, Object>> query = handle.createQuery(
                                String.format(builder.toString(), getTableName())
                        );

                        if (StringUtil.isNotEmpty(criteria.getName())) {
                            query.bind("name", "%" + criteria.getName().trim() + "%");
                        }
                        if (StringUtil.isNotEmpty(criteria.getOwner())) {
                            query.bind("owner", "%" + criteria.getOwner().trim() + "%");
                        }
                        if (criteria.getEnabled() != null) {
                            query.bind("enabled", criteria.getEnabled());
                        }
                        if (criteria.getCreateTimeStart() != null) {
                            query.bind("create_date_start", criteria.getCreateTimeStart());
                        }
                        if (criteria.getCreateTimeEnd() != null) {
                            query.bind("create_date_end", criteria.getCreateTimeEnd());
                        }

                        return query.map(new ResultSetMapper<RecInstance>() {
                            @Override
                            public RecInstance map(int index, ResultSet r, StatementContext ctx) throws SQLException {
                                try {
                                    RecInstance entry = new RecInstance();
                                    entry.setId(r.getString("id"));
                                    entry.setName(r.getString("name"));
                                    entry.setNum(r.getInt("num"));
                                    entry.setOwner(r.getString("owner"));
                                    entry.setCreateTime(new DateTime(r.getTimestamp("create_date")));
                                    entry.setUpdateTime(new DateTime(r.getTimestamp("update_date")));
                                    entry.setEnabled(r.getBoolean("enabled"));
                                    byte[] cBytes = r.getBytes("strategies");
                                    Map<String, RecStrategy> strategies = jsonMapper.readValue(cBytes,
                                            new TypeReference<Map<String, RecStrategy>>() {
                                            });
                                    entry.setRecStrategys(strategies);
                                    return entry;
                                } catch (IOException e) {
                                    throw Throwables.propagate(e);
                                }
                            }
                        }).fold(Lists.newArrayList(), new Folder3<ArrayList<RecInstance>, RecInstance>() {
                            @Override
                            public ArrayList<RecInstance> fold(
                                    ArrayList<RecInstance> retVal,
                                    RecInstance rs,
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
    public void insert(final RecInstance entry) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format(
                                            "INSERT INTO %s (" + ALL_FIELDS + ") " +
                                                    " VALUES (:id, :name, :num, :owner, :create_date, :update_date, :enabled, :strategies)",
                                            getTableName()
                                    )
                            )
                                    .bind("id", entry.getId())
                                    .bind("name", entry.getName())
                                    .bind("num", entry.getNum())
                                    .bind("owner", entry.getOwner())
                                    .bind("create_date", new Timestamp(entry.getCreateTime().getMillis()))
                                    .bind("update_date", new Timestamp(entry.getUpdateTime().getMillis()))
                                    .bind("enabled", entry.getEnabled())
                                    .bind("strategies", jsonMapper.writeValueAsBytes(entry.getRecStrategys()))
                                    .execute();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception insert Recommend Instance %s[%s]", entry.getName(), entry.getId());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(final RecInstance entry) {
        try {
            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(
                                    String.format("UPDATE %s SET name=:name, num=:num, owner=:owner, update_date=:update_date, enabled = :enabled, strategies=:strategies WHERE id = :id",
                                            getTableName())
                            )
                                    .bind("id", entry.getId())
                                    .bind("name", entry.getName())
                                    .bind("num", entry.getNum())
                                    .bind("owner", entry.getOwner())
                                    .bind("update_date", new Timestamp(entry.getUpdateTime().getMillis()))
                                    .bind("enabled", entry.getEnabled())
                                    .bind("strategies", jsonMapper.writeValueAsBytes(entry.getRecStrategys()))
                                    .execute();
                            handle.commit();
                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e, "Exception update RecInstance %s[%s]", entry.getName(), entry.getId());
            return false;
        }
        return true;
    }

    public void delete(String id) {
        try {
            dbi.open().execute("DELETE FROM %s WHERE id=%s", getTableName(), id);
        } catch (Exception e) {
            log.error(e, "Exception delete RecInstance with id [%s]", id);
        }
    }

    private String getTableName() {
        return dbTables.get().getOperatorProcessTable();
    }
}
