package io.sugo.pio.overlord;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 */
public class MetadataTaskStorage implements TaskStorage {
    private static final MetadataStorageActionHandlerTypes<Task, TaskStatus> TASK_TYPES = new MetadataStorageActionHandlerTypes<Task, TaskStatus>()
    {
        @Override
        public TypeReference<Task> getEntryType()
        {
            return new TypeReference<Task>()
            {
            };
        }

        @Override
        public TypeReference<TaskStatus> getStatusType()
        {
            return new TypeReference<TaskStatus>()
            {
            };
        }

    };

    private final MetadataStorageConnector metadataStorageConnector;
    private final MetadataStorageActionHandler<Task, TaskStatus> handler;


    @Inject
    public MetadataTaskStorage(
            final MetadataStorageConnector metadataStorageConnector,
            final MetadataStorageActionHandlerFactory factory
    ) {
        this.metadataStorageConnector = metadataStorageConnector;
        this.handler = factory.create(MetadataStorageTablesConfig.TASK_ENTRY_TYPE, TASK_TYPES);
    }

    @LifecycleStart
    public void start() {
        metadataStorageConnector.createTaskTables();
    }

    @LifecycleStop
    public void stop() {

    }

    @Override
    public void insert(Task task, TaskStatus status) throws EntryExistsException {
    }

    @Override
    public void setStatus(TaskStatus status) {

    }

    @Override
    public Optional<TaskStatus> getStatus(String taskid) {
        return null;
    }

    @Override
    public List<Task> getActiveTasks() {
        return null;
    }
}
