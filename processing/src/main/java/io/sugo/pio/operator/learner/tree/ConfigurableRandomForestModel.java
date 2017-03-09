/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 * <p>
 * Complete list of developers available at our web site:
 * <p>
 * http://rapidminer.com
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
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

    @JsonProperty
    public String getDataModel() {
        return model.toString();
    }

}
