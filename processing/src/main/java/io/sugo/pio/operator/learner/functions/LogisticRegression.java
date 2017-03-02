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
package io.sugo.pio.operator.learner.functions;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.learner.AbstractLearner;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.operator.performance.PerformanceVector;
import io.sugo.pio.parameter.*;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.tools.math.optimization.ec.es.ESOptimization;

import java.util.List;


/**
 * This operator determines a logistic regression model.
 */
public class LogisticRegression extends AbstractLearner {

    /**
     * The parameter name for &quot;Determines whether to include an intercept.&quot;
     */
    public static final String PARAMETER_ADD_INTERCEPT = "add_intercept";

    /**
     * The parameter name for &quot;Determines whether to return the performance.&quot;
     */
    public static final String PARAMETER_RETURN_PERFORMANCE = "return_model_performance";

    /**
     * The parameter name for &quot;The type of start population initialization.&quot;
     */
    public static final String PARAMETER_START_POPULATION_TYPE = "start_population_type";

    /**
     * The parameter name for &quot;Stop after this many evaluations&quot;
     */
    public static final String PARAMETER_MAX_GENERATIONS = "max_generations";

    /**
     * The parameter name for &quot;Stop after this number of generations without improvement (-1:
     * optimize until max_iterations).&quot;
     */
    public static final String PARAMETER_GENERATIONS_WITHOUT_IMPROVAL = "generations_without_improval";

    /**
     * The parameter name for &quot;The population size (-1: number of examples)&quot;
     */
    public static final String PARAMETER_POPULATION_SIZE = "population_size";

    /**
     * The parameter name for &quot;The fraction of the population used for tournament
     * selection.&quot;
     */
    public static final String PARAMETER_TOURNAMENT_FRACTION = "tournament_fraction";

    /**
     * The parameter name for &quot;Indicates if the best individual should survive (elititst
     * selection).&quot;
     */
    public static final String PARAMETER_KEEP_BEST = "keep_best";

    /**
     * The parameter name for &quot;The type of the mutation operator.&quot;
     */
    public static final String PARAMETER_MUTATION_TYPE = "mutation_type";

    /**
     * The parameter name for &quot;The type of the selection operator.&quot;
     */
    public static final String PARAMETER_SELECTION_TYPE = "selection_type";

    /**
     * The parameter name for &quot;The probability for crossovers.&quot;
     */
    public static final String PARAMETER_CROSSOVER_PROB = "crossover_prob";

    /**
     * The parameter name for &quot;Indicates if a dialog with a convergence plot should be
     * drawn.&quot;
     */
    public static final String PARAMETER_SHOW_CONVERGENCE_PLOT = "show_convergence_plot";

    private PerformanceVector estimatedPerformance;

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.LogisticRegression.name");
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.LogisticRegression.description");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public Model learn(ExampleSet exampleSet) throws OperatorException {
        RandomGenerator random = RandomGenerator.getRandomGenerator(this);
        LogisticRegressionOptimization optimization = new LogisticRegressionOptimization(exampleSet,
                getParameterAsBoolean(PARAMETER_ADD_INTERCEPT), getParameterAsInt(PARAMETER_START_POPULATION_TYPE),
                getParameterAsInt(PARAMETER_MAX_GENERATIONS), getParameterAsInt(PARAMETER_GENERATIONS_WITHOUT_IMPROVAL),
                getParameterAsInt(PARAMETER_POPULATION_SIZE), getParameterAsInt(PARAMETER_SELECTION_TYPE),
                getParameterAsDouble(PARAMETER_TOURNAMENT_FRACTION), getParameterAsBoolean(PARAMETER_KEEP_BEST),
                getParameterAsInt(PARAMETER_MUTATION_TYPE), getParameterAsDouble(PARAMETER_CROSSOVER_PROB),
                getParameterAsBoolean(PARAMETER_SHOW_CONVERGENCE_PLOT), random, this);
        LogisticRegressionModel model = optimization.train();
        estimatedPerformance = optimization.getPerformance();
        return model;
    }

    @Override
    public boolean canEstimatePerformance() {
        return true;
    }

    @Override
    public PerformanceVector getEstimatedPerformance() throws OperatorException {
        if (getParameterAsBoolean(PARAMETER_RETURN_PERFORMANCE)) {
            if (estimatedPerformance != null) {
                return estimatedPerformance;
            }
        }
        throw new UserError(this, "pio.error.operator.learner_cannot_estimate",
                getName(), "could not deliver optimization performance.");
    }

    @Override
    public Class<? extends PredictionModel> getModelClass() {
        return LogisticRegressionModel.class;
    }

    @Override
    public boolean supportsCapability(OperatorCapability lc) {
        if (lc == OperatorCapability.NUMERICAL_ATTRIBUTES) {
            return true;
        }
        if (lc == OperatorCapability.BINOMINAL_LABEL) {
            return true;
        }
        if (lc == OperatorCapability.WEIGHTED_EXAMPLES) {
            return true;
        }
        return false;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeBoolean(PARAMETER_ADD_INTERCEPT,
                I18N.getMessage("pio.LogisticRegression.add_intercept"),
//                "Determines whether to include an intercept.",
                true));

        types.add(new ParameterTypeCategory(PARAMETER_START_POPULATION_TYPE,
                I18N.getMessage("pio.LogisticRegression.start_population_type"),
//                "The type of start population initialization.",
                ESOptimization.POPULATION_INIT_TYPES, ESOptimization.INIT_TYPE_RANDOM));
        types.add(new ParameterTypeInt(PARAMETER_MAX_GENERATIONS,
                I18N.getMessage("pio.LogisticRegression.max_generations"),
//                "Stop after this many evaluations",
                1, Integer.MAX_VALUE,
                10000));
        types.add(new ParameterTypeInt(PARAMETER_GENERATIONS_WITHOUT_IMPROVAL,
                I18N.getMessage("pio.LogisticRegression.generations_without_improval"),
//                "Stop after this number of generations without improvement (-1: optimize until max_iterations).",
                -1, Integer.MAX_VALUE, 300));
        types.add(new ParameterTypeInt(PARAMETER_POPULATION_SIZE,
                I18N.getMessage("pio.LogisticRegression.population_size"),
//                "The population size (-1: number of examples)",
                -1, Integer.MAX_VALUE, 3));
        types.add(new ParameterTypeDouble(PARAMETER_TOURNAMENT_FRACTION,
                I18N.getMessage("pio.LogisticRegression.tournament_fraction"),
//                "The fraction of the population used for tournament selection.",
                0.0d, Double.POSITIVE_INFINITY, 0.75d));
        types.add(new ParameterTypeBoolean(PARAMETER_KEEP_BEST,
                I18N.getMessage("pio.LogisticRegression.keep_best"),
//                "Indicates if the best individual should survive (elititst selection).",
                true));
        types.add(new ParameterTypeCategory(PARAMETER_MUTATION_TYPE,
                I18N.getMessage("pio.LogisticRegression.mutation_type"),
//                "The type of the mutation operator.",
                ESOptimization.MUTATION_TYPES, ESOptimization.GAUSSIAN_MUTATION));
        types.add(new ParameterTypeCategory(PARAMETER_SELECTION_TYPE,
                I18N.getMessage("pio.LogisticRegression.selection_type"),
//                "The type of the selection operator.",
                ESOptimization.SELECTION_TYPES, ESOptimization.TOURNAMENT_SELECTION));
        types.add(new ParameterTypeDouble(PARAMETER_CROSSOVER_PROB,
                I18N.getMessage("pio.LogisticRegression.crossover_prob"),
//                "The probability for crossovers.",
                0.0d, 1.0d, 1.0d));

        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));

        types.add(new ParameterTypeBoolean(PARAMETER_SHOW_CONVERGENCE_PLOT,
                I18N.getMessage("pio.LogisticRegression.show_convergence_plot"),
//                "Indicates if a dialog with a convergence plot should be drawn.",
                false));

        // deprecated parameters
        ParameterType type = new ParameterTypeBoolean(PARAMETER_RETURN_PERFORMANCE,
                I18N.getMessage("pio.LogisticRegression.return_model_performance"),
//                "Determines whether to return the performance.",
                true);
        type.setDeprecated();
        types.add(type);

        return types;
    }
}
