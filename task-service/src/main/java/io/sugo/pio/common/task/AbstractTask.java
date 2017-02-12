package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import io.sugo.pio.common.TaskToolbox;
import io.sugo.pio.guice.EnginesConfig;
import io.sugo.pio.guice.ExtensionsConfig;
import io.sugo.pio.guice.GuiceInjectors;
import io.sugo.pio.initialization.Initialization;
import io.sugo.pio.query.QueryRunner;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 */
public abstract class AbstractTask<Q> implements Task<Q> {
    @JsonIgnore
    private final String id;

    @JsonIgnore
    private final TaskResource taskResource;

    private final Map<String, Object> context;

    protected AbstractTask(String id, Map<String, Object> context)
    {
        this(id, null, context);
    }

    protected AbstractTask(
            String id,
            TaskResource taskResource,
            Map<String, Object> context
    )
    {
        this.id = Preconditions.checkNotNull(id, "id");
        this.taskResource = taskResource == null ? new TaskResource(id, 1) : taskResource;
        this.context = context;
    }

    @JsonProperty
    @Override
    public String getId()
    {
        return id;
    }

    @JsonProperty("resource")
    @Override
    public TaskResource getTaskResource()
    {
        return taskResource;
    }

    @Override
    public <R> QueryRunner<Q, R> getQueryRunner()
    {
        return null;
    }

    @Override
    public String getClasspathPrefix() {
        return null;
    }

    @Override
    public boolean canRestore()
    {
        return false;
    }

    @Override
    public void stopGracefully()
    {
        // Should not be called when canRestore = false.
        throw new UnsupportedOperationException("Cannot stop gracefully");
    }

    @Override
    @JsonProperty
    public Map<String, Object> getContext()
    {
        return context;
    }

    @Override
    public Object getContextValue(String key)
    {
        return context == null ? null : context.get(key);
    }
}
