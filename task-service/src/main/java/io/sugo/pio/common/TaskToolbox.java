package io.sugo.pio.common;

import io.sugo.pio.server.coordination.DataServerAnnouncer;

/**
 * Stuff that may be needed by a Task in order to conduct its business.
 */
public class TaskToolbox {
    private final DataServerAnnouncer segmentAnnouncer;

    public TaskToolbox(
            DataServerAnnouncer segmentAnnouncer
    ) {
        this.segmentAnnouncer = segmentAnnouncer;
    }

    public DataServerAnnouncer getSegmentAnnouncer()
    {
        return segmentAnnouncer;
    }
}
