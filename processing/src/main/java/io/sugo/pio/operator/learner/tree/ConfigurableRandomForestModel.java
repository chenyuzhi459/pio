package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.SimplePredictionModel;
import io.sugo.pio.operator.learner.meta.ConfidenceVoteModel;
import io.sugo.pio.operator.learner.meta.MetaModel;
import io.sugo.pio.operator.learner.meta.SimpleVoteModel;

import java.util.List;


/**
 * Random forest that can be configured to either use majority voting or confidence based voting for
 * its prediction.
 *
 * @since 7.0.0
 */
public class ConfigurableRandomForestModel extends SimplePredictionModel implements MetaModel {

    public enum VotingStrategy {
        MAJORITY_VOTE("majority vote"), CONFIDENCE_VOTE("confidence vote");

        private final String value;

        private VotingStrategy(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static final long serialVersionUID = 1L;

    /** The wrapped voting meta model. */
    @JsonProperty
    private final SimplePredictionModel model;

    public ConfigurableRandomForestModel(ExampleSet exampleSet, List<? extends TreeModel> models, VotingStrategy strategy) {
        super(exampleSet, ExampleSetUtilities.SetsCompareOption.EQUAL,
                ExampleSetUtilities.TypesCompareOption.ALLOW_SAME_PARENTS);
        switch (strategy) {
            case MAJORITY_VOTE:
                model = new SimpleVoteModel(exampleSet, models);
                break;
            default:
                model = new ConfidenceVoteModel(exampleSet, models);
        }
    }

    @Override
    public List<? extends Model> getModels() {
        return ((MetaModel) model).getModels();
    }

    @Override
    public List<String> getModelNames() {
        return ((MetaModel) model).getModelNames();
    }

    @Override
    public String getName() {
        return "Random Forest Model";
    }

    @Override
    public double predict(Example example) throws OperatorException {
        return model.predict(example);
    }

    @Override
    public String toString() {
        return model.toString();
    }

}
