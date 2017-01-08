package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.common.TaskStatus;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 */
public class NoopTask implements Task {
    private static final int defaultRunTime = 2500;

    @JsonIgnore
    private final String id;

    @JsonIgnore
    private final long runTime;

    @JsonCreator
    public NoopTask(@JsonProperty("id") String id,
                    @JsonProperty("runTime") long runTime) {
        this.id = (id == null ? String.format("noop_%s_%s", new DateTime(), UUID.randomUUID().toString()) : id);
        this.runTime = (runTime == 0) ? defaultRunTime : runTime;
    }

    @Override
    public TaskStatus run() throws Exception {
        Thread.sleep(runTime);

        return TaskStatus.success(getId());
    }

    @Override
    public String getId() {
        return id;
    }
}
