package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.common.TaskStatus;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.UUID;

/**
 */
public class NoopTask extends AbstractTask {
    private static final int defaultRunTime = 2500;

    @JsonIgnore
    private final long runTime;

    @JsonCreator
    public NoopTask(@JsonProperty("id") String id,
                    @JsonProperty("runTime") long runTime,
                    @JsonProperty("context") Map<String, Object> context) {
        super(
                id == null ? String.format("noop_%s_%s", new DateTime(), UUID.randomUUID().toString()) : id,
                context
        );
        this.runTime = (runTime == 0) ? defaultRunTime : runTime;
    }


    @Override
    public boolean isReady() throws Exception {
        return true;
    }

    @Override
    public TaskStatus run() throws Exception {
        Thread.sleep(runTime);

        return TaskStatus.success(getId());
    }
}
