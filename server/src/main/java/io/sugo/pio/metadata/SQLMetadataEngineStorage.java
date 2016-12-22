package io.sugo.pio.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.engine.EngineInstance;
import io.sugo.pio.guice.ManageLifecycle;
import io.sugo.pio.server.EngineStorage;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.util.ByteArrayMapper;

import java.util.List;
import java.util.Map;

/**
 */
@ManageLifecycle
public class SQLMetadataEngineStorage implements EngineStorage {
    private final SQLMetadataConnector connector;
    private final ObjectMapper jsonMapper;
    private final MetadataStorageTablesConfig config;
    private final String statement;

    @Inject
    public SQLMetadataEngineStorage(
            SQLMetadataConnector connector,
            ObjectMapper jsonMapper,
            MetadataStorageTablesConfig config
    ) {
        this.connector = connector;
        this.jsonMapper = jsonMapper;
        this.config = config;
        this.statement = String.format(
                "INSERT INTO %s (id, payload) "
                        + "VALUES (:id, :payload)",
                config.getEngineTable()
        );
    }

    @LifecycleStart
    public void start() {
        connector.createEngineTable();
    }

    @LifecycleStop
    public void stop() {
        // do nothing
    }

    @Override
    public void register(EngineInstance engineInstance) {
        Preconditions.checkNotNull(engineInstance, "engineInstance");

        try {
            final DBI dbi = connector.getDBI();
            List<Map<String, Object>> exists = dbi.withHandle(
                    new HandleCallback<List<Map<String, Object>>>() {
                        @Override
                        public List<Map<String, Object>> withHandle(Handle handle) throws Exception {
                            return handle.createQuery(
                                    String.format("SELECT id FROM %s WHERE id=:id", config.getEngineTable())
                            )
                                    .bind("id", engineInstance.getId())
                                    .list();
                        }
                    }
            );

            if (!exists.isEmpty()) {
                return;
            }

            dbi.withHandle(
                    new HandleCallback<Void>() {
                        @Override
                        public Void withHandle(Handle handle) throws Exception {
                            handle.createStatement(statement)
                                    .bind("id", engineInstance.getId())
                                    .bind("payload", jsonMapper.writeValueAsBytes(engineInstance))
                                    .execute();

                            return null;
                        }
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<EngineInstance> get(String id) {
        Preconditions.checkNotNull(id, "id");

        return connector.retryWithHandle(
                new HandleCallback<Optional<EngineInstance>>() {
                    @Override
                    public Optional<EngineInstance> withHandle(Handle handle) throws Exception {
                        byte[] res = handle.createQuery(
                                String.format("SELECT payload FROM %s WHERE id = :id", config.getEngineTable())
                        )
                                .bind("id", id)
                                .map(ByteArrayMapper.FIRST)
                                .first();

                        return Optional.fromNullable(
                                res == null ? null : jsonMapper.<EngineInstance>readValue(res, EngineInstance.class)
                        );
                    }
                }
        );
    }
}
