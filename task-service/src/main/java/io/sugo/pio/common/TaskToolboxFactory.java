package io.sugo.pio.common;

import com.google.inject.Inject;
import io.sugo.pio.common.task.Task;
import io.sugo.pio.server.coordination.DataServerAnnouncer;

/**
 */
public class TaskToolboxFactory {
    private final DataServerAnnouncer segmentAnnouncer;

    @Inject
    public TaskToolboxFactory(
            DataServerAnnouncer segmentAnnouncer
    ) {
        this.segmentAnnouncer = segmentAnnouncer;
    }

    public TaskToolbox build(Task task) {
        return new TaskToolbox(segmentAnnouncer);
    }
}
