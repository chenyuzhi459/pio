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
public class ModelServingTask extends AbstractTask<PredictionQueryObject> {
    private final ModelFactory modelFactory;
    private final String modelId;

    private PredictionModel model;

    @JsonIgnore
    private final Object handoffCondition = new Object();

    @JsonCreator
    public ModelServingTask(
            @JsonProperty("id") String id,
            @JsonProperty("modelId") String modelId,
            @JsonProperty("context") Map<String, Object> context,
            @JsonProperty("modelFactory") ModelFactory modelFactory) {
        super(id, context);
        this.modelId = modelId;
        this.modelFactory = modelFactory;
    }

    @Override
    public boolean isReady() throws Exception {
        return true;
    }

    @Override
    public TaskStatus run(TaskToolbox toolbox) throws Exception {
        model = modelFactory.loadModel();

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
    public QueryRunner<PredictionQueryObject, Object> getQueryRunner()
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

    class PredictQueryRunner implements QueryRunner<PredictionQueryObject, Object> {
        private final PredictionModel predictionModel;

        PredictQueryRunner(PredictionModel predictionModel) {
            this.predictionModel = predictionModel;
        }

        @Override
        public Object run(Query<PredictionQueryObject> query, Map<String, Object> responseContext) {
            if (null != predictionModel) {
                return predictionModel.predict(query.getQueryObject());
            }
            return null;
        }
    }
}
