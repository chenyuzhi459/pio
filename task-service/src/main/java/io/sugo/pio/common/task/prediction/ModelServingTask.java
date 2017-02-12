package io.sugo.pio.common.task.prediction;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.common.TaskToolbox;
import io.sugo.pio.common.task.AbstractTask;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;
import io.sugo.pio.query.Query;
import io.sugo.pio.query.QueryRunner;
import io.sugo.pio.server.coordination.DataServerAnnouncer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 */
public class ModelServingTask<R> extends AbstractTask<PredictionQueryObject> {
    private final ModelFactory modelFactory;
    private final String modelId;

    private PredictionModel<R> model;

    @JsonIgnore
    private final Object handoffCondition = new Object();

    @JsonIgnore
    private final ObjectMapper jsonMapper;

    @JsonCreator
    public ModelServingTask(
            @JsonProperty("id") String id,
            @JsonProperty("modelId") String modelId,
            @JsonProperty("context") Map<String, Object> context,
            @JacksonInject ObjectMapper jsonMapper,
            @JsonProperty("modelFactory") ModelFactory<R> modelFactory) {
        super(id, context);
        this.modelId = modelId;
        this.modelFactory = modelFactory;

        this.jsonMapper = Preconditions.checkNotNull(jsonMapper, "null ObjectMappper");
    }

    @Override
    public boolean isReady() throws Exception {
        return true;
    }

    @Override
    public TaskStatus run(TaskToolbox toolbox) throws Exception {
        final ClassLoader loader = buildClassLoader(toolbox);
        model = invokeForeignLoader("io.sugo.pio.common.task.prediction.ModelServingTask$ModelLoading",
                new String[]{
                    jsonMapper.writeValueAsString(modelFactory)
                },
                loader);

        DataServerAnnouncer announcer =  toolbox.getSegmentAnnouncer();
        announcer.announce(modelId);
        synchronized (handoffCondition) {
            handoffCondition.wait();
        }

        return TaskStatus.success(getId());
    }

    @JsonProperty
    public String getModelId() {
        return modelId;
    }

    @JsonProperty
    public ModelFactory getModelFactory() {
        return modelFactory;
    }

    @Override
    public QueryRunner<PredictionQueryObject, R> getQueryRunner()
    {
        return new PredictQueryRunner(model);
    }

    @Override
    public void stopGracefully()
    {
        synchronized (handoffCondition) {
            handoffCondition.notifyAll();
        }
    }

    class PredictQueryRunner implements QueryRunner<PredictionQueryObject, R> {
        private final PredictionModel<R> predictionModel;

        PredictQueryRunner(PredictionModel<R> predictionModel) {
            this.predictionModel = predictionModel;
        }

        @Override
        public R run(Query<PredictionQueryObject> query, Map<String, Object> responseContext) {
            if (null != predictionModel) {
                return predictionModel.predict(query.getQueryObject());
            }
            return null;
        }
    }

    private static <InputType, OutputType> OutputType invokeForeignLoader(
            final String clazzName,
            final InputType input,
            final ClassLoader loader
    )
    {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            final Class<?> clazz = loader.loadClass(clazzName);
            final Method method = clazz.getMethod("load", input.getClass());
            return (OutputType) method.invoke(null, input);
        }
        catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    public static class ModelLoading {
        public static PredictionModel load(String[] args) throws Exception
        {
            String modelFactoryJson = args[0];
            ObjectMapper objectMapper = ModelServingTaskConfig.JSON_MAPPER;
            ModelFactory modelFactory = objectMapper.readValue(modelFactoryJson, ModelFactory.class);
            return modelFactory.loadModel();
        }
    }
}
