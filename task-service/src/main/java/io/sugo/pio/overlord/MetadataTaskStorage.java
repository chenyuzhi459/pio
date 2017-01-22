package io.sugo.pio.overlord;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.metamx.common.Pair;
import com.metamx.common.lifecycle.LifecycleStart;
import com.metamx.common.lifecycle.LifecycleStop;
import com.metamx.common.logger.Logger;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.metadata.*;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
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

    private static final Logger log = new Logger(SQLMetadataConnector.class);

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
        Preconditions.checkNotNull(task, "task");
        Preconditions.checkNotNull(status, "status");
        Preconditions.checkArgument(
                task.getId().equals(status.getId()),
                "Task/Status ID mismatch[%s/%s]",
                task.getId(),
                status.getId()
        );

        log.info("Inserting task %s with status: %s", task.getId(), status);

        try {
            handler.insert(
                    task.getId(),
                    new DateTime(),
                    task,
                    status.isRunnable(),
                    status
            );
        }
        catch (Exception e) {
            if(e instanceof EntryExistsException) {
                throw (EntryExistsException) e;
            } else {
                Throwables.propagate(e);
            }
        }
    }

    @Override
    public void setStatus(TaskStatus status) {
        Preconditions.checkNotNull(status, "status");

        log.info("Updating task %s to status: %s", status.getId(), status);

        final boolean set = handler.setStatus(
                status.getId(),
                status.isRunnable(),
                status
        );
        if (!set) {
            throw new IllegalStateException(String.format("Active task not found: %s", status.getId()));
        }
    }

    @Override
    public Optional<TaskStatus> getStatus(String taskId) {
        return handler.getStatus(taskId);
    }

    @Override
    public List<Task> getActiveTasks() {
        return ImmutableList.copyOf(
                Iterables.transform(
                        Iterables.filter(
                                handler.getActiveEntriesWithStatus(),
                                new Predicate<Pair<Task, TaskStatus>>()
                                {
                                    @Override
                                    public boolean apply(
                                            @Nullable Pair<Task, TaskStatus> input
                                    )
                                    {
                                        return input.rhs.isRunnable();
                                    }
                                }
                        ),
                        new Function<Pair<Task, TaskStatus>, Task>()
                        {
                            @Nullable
                            @Override
                            public Task apply(@Nullable Pair<Task, TaskStatus> input)
                            {
                                return input.lhs;
                            }
                        }
                )
        );
    }
}
