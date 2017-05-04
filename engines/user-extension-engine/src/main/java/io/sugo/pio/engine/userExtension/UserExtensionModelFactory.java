package io.sugo.pio.engine.userExtension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.prediction.ModelFactory;
import io.sugo.pio.engine.prediction.PredictionModel;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

/**
 */
public class UserExtensionModelFactory implements ModelFactory<UserExtensionResult> {
    private final Repository repository;

    @JsonCreator
    public UserExtensionModelFactory(@JsonProperty("repository") Repository repository) {
        this.repository = repository;
    }

    @Override
    public PredictionModel<UserExtensionResult> loadModel() {
        return new UserExtensionPredictionModel();
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    static class UserExtensionPredictionModel implements PredictionModel<UserExtensionResult> {

        public UserExtensionResult predict(PredictionQueryObject query) {
            UserExtensionQuery userExtensionQuery = (UserExtensionQuery) query;
            return new UserExtensionResult();
        }
    }
}
