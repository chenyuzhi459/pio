package io.sugo.pio.common.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.common.TaskStatus;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.query.QueryRunner;

import java.util.Map;

/**
 */
public class ModelServingTask<Q, R> extends AbstractTask {
    private final Repository repository;
    private final ModelFactory modelFactory;

    @JsonCreator
    public ModelServingTask(
            @JsonProperty("id") String id,
            @JsonProperty("context") Map<String, Object> context,
            @JsonProperty("repository") Repository repository,
            @JsonProperty("modelFactory") ModelFactory<Q, R> modelFactory) {
        super(id, context);
        this.repository = repository;
        this.modelFactory = modelFactory;
    }

    @Override
    public boolean isReady() throws Exception {
        return false;
    }

    @Override
    public TaskStatus run() throws Exception {
        return null;
    }

    @Override
    public QueryRunner<Q, R> getQueryRunner()
    {
        PredictionModel<Q, R> predictionModel = modelFactory.loadModel(repository);
        return new PredictQueryRunner(predictionModel);
    }

    class PredictQueryRunner implements QueryRunner<Q, R> {
        private final PredictionModel<Q, R> predictionModel;

        PredictQueryRunner(PredictionModel<Q, R> predictionModel) {
            this.predictionModel = predictionModel;
        }


        @Override
        public R run(Q query, Map<String, Object> responseContext) {
            return predictionModel.predict(query);
        }
    }

}
