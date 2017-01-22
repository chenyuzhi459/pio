package io.sugo.pio.common.task.prediction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

import java.util.Map;

/**
 */
public class ModelServingTask<R> extends AbstractTask<PredictionQueryObject> {
    private final Repository repository;
    private final ModelFactory modelFactory;
    private final String modelId;

    @JsonIgnore
    private PredictionModel<R> model;

    @JsonIgnore
    private final Object handoffCondition = new Object();

    @JsonCreator
    public ModelServingTask(
            @JsonProperty("id") String id,
            @JsonProperty("modelId") String modelId,
            @JsonProperty("context") Map<String, Object> context,
            @JsonProperty("repository") Repository repository,
            @JsonProperty("modelFactory") ModelFactory<R> modelFactory) {
        super(id, context);
        this.modelId = modelId;
        this.repository = repository;
        this.modelFactory = modelFactory;
    }

    @Override
    public boolean isReady() throws Exception {
        return true;
    }

    @Override
    public TaskStatus run(TaskToolbox toolbox) throws Exception {
        DataServerAnnouncer announcer =  toolbox.getSegmentAnnouncer();
        model = modelFactory.loadModel(repository);
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
    public Repository getRepository() {
        return repository;
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

}
