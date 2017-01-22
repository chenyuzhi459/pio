package io.sugo.pio.spark.operator.spark;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;
import io.sugo.pio.spark.datahandler.mapreducehdfs.MapReduceHDFSHandler.SparkOperation;
import io.sugo.pio.spark.transfer.model.ModelTransferObject;
import io.sugo.pio.spark.transfer.parameter.SparkDecisionTreeParameter;
import io.sugo.pio.spark.transfer.parameter.SparkParameter;

import java.util.List;

/**
 */
public class SparkDecisionTree extends AbstractSparkLearner {
    public static final String PARAMETER_MAX_DEPTH = "maximal_depth";
    public static final String PARAMETER_MIN_GAIN = "minimal_gain";
    public static final String PARAMETER_MIN_INSTANCES = "minimal_size_for_split";
    public static final String PARAMETER_NUM_CLASSES = "number_of_classes";
    public static final String PARAMETER_MAX_BINS = "maximum_bins";
    public static final String PARAMETER_MAX_MEMORY = "maximum_memory_in_MB";
    public static final String PARAMETER_SUBSAMPLING_RATE = "subsampling_rate";
    public static final String PARAMETER_USE_ID_CACHE = "use_node_id_cache";
    public static final String PARAMETER_DRIVER_MEMORY = "driver_memory_(MB)";
    public static final String PARAMETER_USE_BINOMINAL_MAPPINGS = "use_binominal_mappings";
    public static final String PARAMETER_IMPURITY = "criterion";
    public static final String[] IMPURITY = new String[]{"Gini", "Entropy"};

    @JsonCreator
    public SparkDecisionTree(
            @JsonProperty("name") String name,
            @JsonProperty("inputPort") InputPort exampleSetInput,
            @JsonProperty("outputPort") OutputPort modelOutput
    ) {
        super(SparkOperation.DecisionTree, name, exampleSetInput, modelOutput);
    }

    @Override
    public boolean canEstimatePerformance() {
        return false;
    }

    @Override
    public boolean canCalculateWeights() {
        return false;
    }

    @Override
    protected void postProcessModel(PredictionModel model, HadoopExampleSet exampleSet) {
    }

    @Override
    protected SparkParameter setupAlgorithmParams(HadoopExampleSet inputHes) {
        String impurity = IMPURITY[getParameterAsInt(PARAMETER_IMPURITY)];
        int maxDepth = getParameterAsInt(PARAMETER_MAX_DEPTH);
        int minInstances = getParameterAsInt(PARAMETER_MIN_INSTANCES);
        double minGain = getParameterAsDouble(PARAMETER_MIN_GAIN);
        int maxBins = getParameterAsInt(PARAMETER_MAX_BINS);
        int maxMemoryInMB = getParameterAsInt(PARAMETER_MAX_MEMORY);
        double subsamplingRate = getParameterAsDouble(PARAMETER_SUBSAMPLING_RATE);
        boolean useNodeIdCache = getParameterAsBoolean(PARAMETER_USE_ID_CACHE);
        boolean useBinominalMappings = getParameterAsBoolean(PARAMETER_USE_BINOMINAL_MAPPINGS);
        SparkDecisionTreeParameter runnerParams = new SparkDecisionTreeParameter();
        runnerParams.setImpurity(impurity);
        runnerParams.setMaxDepth(maxDepth);
        runnerParams.setMinInstances(minInstances);
        runnerParams.setMinGain(minGain);
        runnerParams.setMaxBins(maxBins);
        runnerParams.setMaxMemoryInMB(maxMemoryInMB);
        runnerParams.setSubsamplingRate(subsamplingRate);
        runnerParams.setUseNodeIdCache(useNodeIdCache);
        runnerParams.setUseBinominalMappings(useBinominalMappings);
        return runnerParams;
    }

    @Override
    protected PredictionModel convertModelFromMTO(ModelTransferObject mto, HadoopExampleSet exampleSet) {
        return null;
    }

    @Override
    public String getFullName() {
        return "SparkDecisionTree";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return "SparkDecisionTree";
    }

    public List<ParameterType> getParameterTypes() {
        List types = super.getParameterTypes();
        types.add(new ParameterTypeCategory(PARAMETER_IMPURITY, "Selects the criterion on which attributes will be selected for splitting.", IMPURITY, 0));
        types.add(new ParameterTypeDouble(PARAMETER_MIN_GAIN, "For a node to be split further, the split must improve at least this much (in terms of information gain).", 0.0D, 1.0D, 0.1D));
        types.add(new ParameterTypeInt(PARAMETER_MAX_DEPTH, "Maximum depth of a tree. Deeper trees are more expressive (potentially allowing higher accuracy), but they are also more costly to train and are more likely to overfit.", 0, 2147483647, 20));
        ParameterTypeInt type = new ParameterTypeInt(PARAMETER_MAX_BINS, "Number of bins used when discretizing continuous features.", 2, 2147483647, 32);
        types.add(type);
        types.add(new ParameterTypeInt(PARAMETER_MIN_INSTANCES, "For a node to be split further, each of its children must receive at least this number of training instances.", 1, 2147483647, 4));
        types.add(new ParameterTypeInt(PARAMETER_MAX_MEMORY, "Amount of memory to be used for collecting sufficient statistics. The default value is conservatively chosen to be 256 MB to allow the decision algorithm to work in most scenarios. Increasing maxMemoryInMB can lead to faster training (if the memory is available) by allowing fewer passes over the data. However, there may be decreasing returns as maxMemoryInMB grows since the amount of communication on each iteration can be proportional to maxMemoryInMB.", 0, 2147483647, 256));
        types.add(new ParameterTypeDouble(PARAMETER_SUBSAMPLING_RATE, "Fraction of the training data used for learning the decision tree. ", 4.9E-324D, 1.0D, 1.0D));
        types.add(new ParameterTypeBoolean(PARAMETER_USE_ID_CACHE, "If this is set to true, the algorithm will avoid passing the current model (tree or trees) to executors on each iteration.", false));
        types.add(new ParameterTypeBoolean(PARAMETER_USE_BINOMINAL_MAPPINGS, "If this is set to true, the algorithm will try to avoid discovering the nominal values. This can decrease the execution time noticeably.Enable this checkbox if you want to train a Tree on only numerical and binominal features and you have provided a correct mapping for every binominal feature in the training data set. Please note that in this case your input data must not contain missing values.", false));
        types.add(new ParameterTypeInt(PARAMETER_DRIVER_MEMORY, "Amount of memory to use for the driver process in MB. You should consider setting this higher if you train on features with many distinct categorical values. Set it to 0 to use the configured default value.", 0, 2147483647, 2048));
        return types;
    }
}
