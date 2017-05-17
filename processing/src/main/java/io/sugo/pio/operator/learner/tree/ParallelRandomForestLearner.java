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

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.operator.learner.tree.ConfigurableRandomForestModel.VotingStrategy;
import io.sugo.pio.operator.preprocessing.sampling.BootstrappingOperator;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.studio.internal.Resources;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.util.OperatorService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


/**
 * This operators learns a random forest. The resulting forest model contains several single random
 * tree models.
 */
public class ParallelRandomForestLearner extends ParallelDecisionTreeLearner {

    private static final Logger logger = new Logger(ParallelRandomForestLearner.class);

    public static final String PARAMETER_USE_HEURISTIC_SUBSET_RATION = "guess_subset_ratio";

    /**
     * The parameter name for &quot;Ratio of randomly chosen attributes to test&quot;
     */
    public static final String PARAMETER_SUBSET_RATIO = "subset_ratio";

    /**
     * The parameter name for the number of trees.
     */
    public static final String PARAMETER_NUMBER_OF_TREES = "number_of_trees";

    /**
     * Voting strategy used to compare trees.
     */
    public static final String PARAMETER_VOTING_STRATEGY = "voting_strategy";

    /*public static final String[] VOTING_STRATEGIES = {VotingStrategy.CONFIDENCE_VOTE.toString(),
            VotingStrategy.MAJORITY_VOTE.toString()};*/
    public static final String[] VOTING_STRATEGIES = {
            I18N.getMessage("pio.ParallelRandomForestLearner.voting_strategy.confidence_vote"),
            I18N.getMessage("pio.ParallelRandomForestLearner.voting_strategy.majority_vote")
    };

    /**
     * The last version which did not allow to specify the voting strategy.
     */
//    public static final OperatorVersion ONLY_MAJORITY_VOTING = new OperatorVersion(6, 5, 0);
    public ParallelRandomForestLearner() {
        super();
    }

    @Override
    public Class<? extends PredictionModel> getModelClass() {
        return ConfigurableRandomForestModel.class;
    }

    @Override
    public Model learn(ExampleSet exampleSet) throws OperatorException {

        logger.info("ParallelRandomForestLearner begin to learn through example set[%s], size[%d]",
                exampleSet.getName(), exampleSet.size());

        // check if the label attribute contains any missing values
        Attribute labelAtt = exampleSet.getAttributes().getLabel();
        exampleSet.recalculateAttributeStatistics(labelAtt);
        if (exampleSet.getStatistics(labelAtt, Statistics.UNKNOWN) > 0) {
            throw new UserError(this, "pio.error.operator.label_miss_values", labelAtt.getName());
        }

        // learn base models
        collectLog("Learn base models.");
        List<TreeModel> baseModels = new LinkedList<TreeModel>();
        int numberOfTrees = getParameterAsInt(PARAMETER_NUMBER_OF_TREES);

        RandomGenerator random = RandomGenerator.getRandomGenerator(this);

        // create callables that build a random tree each
        collectLog("Create callables that build a random tree each.");
        List<Callable<TreeModel>> tasks = new ArrayList<>(numberOfTrees);
        for (int i = 0; i < numberOfTrees; i++) {
            tasks.add(new TreeCallable(exampleSet, random.nextInt()));
        }

        if (Resources.getConcurrencyContext(this).getParallelism() > 1 && tasks.size() > 1) {
            logger.info("ParallelRandomForestLearner execute in parallel of %d trees.", numberOfTrees);
            collectLog("Execute in parallel of " + numberOfTrees + " trees.");

            // execute in parallel
            List<TreeModel> results = null;
            try {
                results = Resources.getConcurrencyContext(this).call(tasks);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof OperatorException) {
                    throw (OperatorException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new OperatorException(cause.getMessage(), cause);
                }
            }
            for (TreeModel result : results) {
                baseModels.add(result);
            }
        } else {
            logger.info("ParallelRandomForestLearner execute in sequential of %d trees.", numberOfTrees);
            collectLog("Execute in sequential of " + numberOfTrees + " trees.");

            // execute sequential
            for (Callable<TreeModel> task : tasks) {
                try {
                    baseModels.add(task.call());
                } catch (Exception e) {
                    if (e instanceof OperatorException) {
                        throw (OperatorException) e;
                    } else if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new OperatorException(e.getMessage(), e);
                    }
                }
            }
        }

        // determine voting strategy
        VotingStrategy strategy = VotingStrategy.MAJORITY_VOTE;
//        if (getCompatibilityLevel().isAbove(ONLY_MAJORITY_VOTING)) {
        String strategyParameter = getParameterAsString(PARAMETER_VOTING_STRATEGY);
        if (VotingStrategy.CONFIDENCE_VOTE.toString().equals(strategyParameter)) {
            strategy = VotingStrategy.CONFIDENCE_VOTE;
        }
        collectLog("Use voting strategy of " + strategy.toString());
//        }

        // create and return model
        collectLog("Create and return model.");
        return new ConfigurableRandomForestModel(exampleSet, baseModels, strategy);
    }

    /**
     * Callable that applies bootstrapping to the example set and then learns a random tree. The
     * callable has its own {@link Random} so that the randomness is independent of execution
     * orders.
     *
     * @author Gisa Schaefer
     */
    private class TreeCallable implements Callable<TreeModel> {

        private final Random callableRandom;
        private ExampleSet exampleSet;

        private TreeCallable(ExampleSet exampleSet, int seed) {
            this.callableRandom = new Random(seed);
            this.exampleSet = exampleSet;
        }

        @Override
        public TreeModel call() throws OperatorException {
            logger.info("ParallelRandomForestLearner apply bootstrapping.");

            // apply bootstrapping
            BootstrappingOperator bootstrapping = getBootstrappingOperator(callableRandom.nextInt(Integer.MAX_VALUE));
            exampleSet = bootstrapping.apply(exampleSet);

            logger.info("ParallelRandomForestLearner learn random tree.");

            // learn random tree
            AbstractParallelTreeBuilder treeBuilder = getTreeBuilder(exampleSet, callableRandom);
            Tree tree = treeBuilder.learnTree(exampleSet);
            return generateModel(exampleSet, tree);
        }
    }

    /**
     * Creates a BootstrappingOperator that has the given local random seed and uses a sample ration
     * of <code>1.0</code>.
     *
     * @param seed
     * @return
     * @throws OperatorException
     */
    private BootstrappingOperator getBootstrappingOperator(int seed) throws OperatorException {
        try {
            BootstrappingOperator bootstrapping = OperatorService.createOperator(BootstrappingOperator.class);
            bootstrapping.setParameter(BootstrappingOperator.PARAMETER_USE_WEIGHTS, "false");
            bootstrapping.setParameter(BootstrappingOperator.PARAMETER_SAMPLE_RATIO, "1.0");
            bootstrapping.setParameter(RandomGenerator.PARAMETER_USE_LOCAL_RANDOM_SEED, "true");
            bootstrapping.setParameter(RandomGenerator.PARAMETER_LOCAL_RANDOM_SEED, "" + seed);
            return bootstrapping;
        } catch (OperatorCreationException e) {
            throw new OperatorException(getName() + ": cannot construct random forest learner: " + e.getMessage());
        }

    }

    /**
     * Constructs a {@link NonParallelPreprocessingTreeBuilder} with termination criteria depending
     * on the exampleSet and preprocessings depending on a random seed.
     *
     * @param exampleSet
     * @param random
     * @return
     * @throws OperatorException
     */
    protected AbstractParallelTreeBuilder getTreeBuilder(ExampleSet exampleSet, Random random) throws OperatorException {
        return new NonParallelPreprocessingTreeBuilder(this, createCriterion(), getTerminationCriteria(exampleSet),
                getPruner(), getSplitPreprocessing(random.nextInt(Integer.MAX_VALUE)),
                getParameterAsBoolean(PARAMETER_PRE_PRUNING), getParameterAsInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES),
                getParameterAsInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT), getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE),
                getExampleSetPreprocessing(random.nextInt(Integer.MAX_VALUE)));
    }

    /**
     * Creates a {@link TreeModel} out of an exampleSet and a tree and names it after the operator.
     *
     * @param exampleSet
     * @param tree
     * @return
     */
    private TreeModel generateModel(ExampleSet exampleSet, Tree tree) {
        TreeModel model = new TreeModel(exampleSet, tree);
        model.setSource(getName());
        return model;
    }

    /**
     * Returns a random feature subset sampling.
     */
    @Override
    public AttributePreprocessing getSplitPreprocessing(int seed) {
        AttributePreprocessing preprocessing = null;
        try {
            preprocessing = new RandomAttributeSubsetPreprocessing(
                    getParameterAsBoolean(PARAMETER_USE_HEURISTIC_SUBSET_RATION),
                    getParameterAsDouble(PARAMETER_SUBSET_RATIO), RandomGenerator.getRandomGenerator(
                    getParameterAsBoolean(RandomGenerator.PARAMETER_USE_LOCAL_RANDOM_SEED), seed));
        } catch (UndefinedParameterError e) {
            // cannot happen
        }
        return preprocessing;
    }

    /**
     * Returns a random feature subset sampling analogously as {@link #getSplitPreprocessing(int)}
     * but the resulting preprocessing is applicable to example set instead of selection arrays.
     *
     * @param seed
     * @return
     */
    public SplitPreprocessing getExampleSetPreprocessing(int seed) {
        SplitPreprocessing preprocessing = null;
        try {
            preprocessing = new RandomSubsetPreprocessing(getParameterAsBoolean(PARAMETER_USE_HEURISTIC_SUBSET_RATION),
                    getParameterAsDouble(PARAMETER_SUBSET_RATIO), RandomGenerator.getRandomGenerator(
                    getParameterAsBoolean(RandomGenerator.PARAMETER_USE_LOCAL_RANDOM_SEED), seed));
        } catch (UndefinedParameterError e) {
            // cannot happen
        }
        return preprocessing;
    }

    @Override
    public boolean supportsCapability(OperatorCapability capability) {
        if (capability == io.sugo.pio.operator.OperatorCapability.BINOMINAL_ATTRIBUTES) {
            return true;
        }
        if (capability == io.sugo.pio.operator.OperatorCapability.POLYNOMINAL_ATTRIBUTES) {
            return true;
        }
        if (capability == io.sugo.pio.operator.OperatorCapability.NUMERICAL_ATTRIBUTES) {
            return true;
        }
        if (capability == io.sugo.pio.operator.OperatorCapability.POLYNOMINAL_LABEL) {
            return true;
        }
        if (capability == io.sugo.pio.operator.OperatorCapability.BINOMINAL_LABEL) {
            return true;
        }
        if (capability == io.sugo.pio.operator.OperatorCapability.WEIGHTED_EXAMPLES) {
            return false;
        }
        return false;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.ParallelRandomForestLearner.name");
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.ParallelRandomForestLearner.description");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.classification;
    }

    @Override
    public int getSequence() {
        return 3;
    }

    @Override
    public List<ParameterType> getParameterTypes() {

        List<ParameterType> types = new LinkedList<ParameterType>();

        ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_TREES,
                I18N.getMessage("pio.ParallelRandomForestLearner.number_of_trees"), 1,
                Integer.MAX_VALUE, 10);
        type.setExpert(false);
        types.add(type);

        types.addAll(super.getParameterTypes());

        type = new ParameterTypeBoolean(PARAMETER_USE_HEURISTIC_SUBSET_RATION,
                I18N.getMessage("pio.ParallelRandomForestLearner.guess_subset_ratio"), true);
        type.setExpert(false).setHidden(true);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_SUBSET_RATIO,
                I18N.getMessage("pio.ParallelRandomForestLearner.subset_ratio"), 0.0d, 1.0d,
                0.2d);
        type.registerDependencyCondition(
                new BooleanParameterCondition(this, PARAMETER_USE_HEURISTIC_SUBSET_RATION, false, false));
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_VOTING_STRATEGY,
                I18N.getMessage("pio.ParallelRandomForestLearner.voting_strategy"),
                VOTING_STRATEGIES, 0);
//		type.registerDependencyCondition(new AboveOperatorVersionCondition(this, ONLY_MAJORITY_VOTING));
        type.setExpert(false).setHidden(true);
        types.add(type);

//        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));

        return types;
    }

   /* @Override
    public OperatorVersion[] getIncompatibleVersionChanges() {
        OperatorVersion[] incompatibleVersions = super.getIncompatibleVersionChanges();
        OperatorVersion[] extendedIncompatibleVersions = Arrays.copyOf(incompatibleVersions,
                incompatibleVersions.length + 1);
        extendedIncompatibleVersions[incompatibleVersions.length] = ONLY_MAJORITY_VOTING;
        return extendedIncompatibleVersions;
    }*/

}
