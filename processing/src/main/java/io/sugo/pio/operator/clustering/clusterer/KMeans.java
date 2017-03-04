package io.sugo.pio.operator.clustering.clusterer;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.*;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.clustering.CentroidClusterModel;
import io.sugo.pio.operator.clustering.ClusterModel;
import io.sugo.pio.operator.learner.CapabilityProvider;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.CapabilityPrecondition;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;
import io.sugo.pio.tools.math.similarity.DistanceMeasureHelper;
import io.sugo.pio.tools.math.similarity.DistanceMeasures;

import java.util.ArrayList;
import java.util.List;


/**
 * This operator represents an implementation of k-means. This operator will create a cluster
 * attribute if not present yet.
 */
public class KMeans extends RMAbstractClusterer implements CapabilityProvider {

    private static final Logger logger = new Logger(KMeans.class);

    /**
     * The parameter name for &quot;the maximal number of clusters&quot;
     */
    public static final String PARAMETER_K = "k";

    private DistanceMeasureHelper measureHelper = new DistanceMeasureHelper(this);
    private DistanceMeasure presetMeasure = null;

    /**
     * The parameter name for &quot;the maximal number of runs of the k method with random
     * initialization that are performed&quot;
     */
    public static final String PARAMETER_MAX_RUNS = "max_runs";

    /**
     * The parameter name for &quot;the maximal number of iterations performed for one run of the k
     * method&quot;
     */
    public static final String PARAMETER_MAX_OPTIMIZATION_STEPS = "max_optimization_steps";

    public KMeans() {
        super();
        getExampleSetInputPort().addPrecondition(new CapabilityPrecondition(this, getExampleSetInputPort()));
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.KMeans.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.KMeans.description");
    }

    /**
     * Overrides the measure specified by the operator parameters. If set to null, parameters will
     * be used again to determine the measure.
     */
    public void setPresetMeasure(DistanceMeasure me) {
        this.presetMeasure = me;
    }

    @Override
    public ClusterModel generateClusterModel(ExampleSet exampleSet) throws OperatorException {
        int k = getParameterAsInt(PARAMETER_K);
        int maxOptimizationSteps = getParameterAsInt(PARAMETER_MAX_OPTIMIZATION_STEPS);
        int maxRuns = getParameterAsInt(PARAMETER_MAX_RUNS);
        boolean kpp = getParameterAsBoolean(KMeanspp.PARAMETER_USE_KPP);

        logger.info("KMeans begin to generate cluster model with parameter k[%d], maxOptimizationSteps[%d], maxRuns[%d].",
                k, maxOptimizationSteps, maxRuns);

        // init operator progress
        getProgress().setTotal(maxRuns * maxOptimizationSteps);

        DistanceMeasure measure;
        if (presetMeasure != null) {
            measure = presetMeasure;
            measure.init(exampleSet);
        } else {
            measure = measureHelper.getInitializedMeasure(exampleSet);
        }

        // checking and creating ids if necessary
        Tools.checkAndCreateIds(exampleSet);

        // additional checks
        Tools.onlyNonMissingValues(exampleSet, "KMeans", this, new String[0]);
        if (exampleSet.size() < k) {
            throw new UserError(this, "pio.error.operator.exampleset_too_small", k);
        }

        // extracting attribute names
        Attributes attributes = exampleSet.getAttributes();
        ArrayList<String> attributeNames = new ArrayList<>(attributes.size());
        for (Attribute attribute : attributes) {
            attributeNames.add(attribute.getName());
        }

        RandomGenerator generator = RandomGenerator.getRandomGenerator(this);
        double minimalIntraClusterDistance = Double.POSITIVE_INFINITY;
        CentroidClusterModel bestModel = null;
        int[] bestAssignments = null;
        double[] values = new double[attributes.size()];

        for (int iter = 0; iter < maxRuns; iter++) {
            CentroidClusterModel model = new CentroidClusterModel(exampleSet, k, attributeNames, measure,
                    getParameterAsBoolean(RMAbstractClusterer.PARAMETER_ADD_AS_LABEL),
                    getParameterAsBoolean(RMAbstractClusterer.PARAMETER_REMOVE_UNLABELED));
            // init centroids by assigning one single, unique example!
            int i = 0;
            if (kpp) {
                KMeanspp kmpp = new KMeanspp(k, exampleSet, measure, generator);
                int[] hilf = kmpp.getStart();
                int i1 = 0;

                for (int id : hilf) {
                    double[] as = getAsDoubleArray(exampleSet.getExample(id), attributes, values);
                    model.assignExample(i1, as);
                    i1++;
                }
            } else {
                for (Integer index : generator.nextIntSetWithRange(0, exampleSet.size(), k)) {
                    model.assignExample(i, getAsDoubleArray(exampleSet.getExample(index), attributes, values));
                    i++;
                }
            }
            model.finishAssign();

            // run optimization steps
            int[] centroidAssignments = new int[exampleSet.size()];
            boolean stable = false;
            for (int step = 0; step < maxOptimizationSteps && !stable; step++) {
                getProgress().step();

                // assign examples to new centroids
                i = 0;
                for (Example example : exampleSet) {
                    double[] exampleValues = getAsDoubleArray(example, attributes, values);
                    double nearestDistance = measure.calculateDistance(model.getCentroidCoordinates(0), exampleValues);
                    int nearestIndex = 0;
                    for (int centroidIndex = 1; centroidIndex < k; centroidIndex++) {
                        double distance = measure.calculateDistance(model.getCentroidCoordinates(centroidIndex),
                                exampleValues);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestIndex = centroidIndex;
                        }
                    }
                    centroidAssignments[i] = nearestIndex;
                    model.getCentroid(nearestIndex).assignExample(exampleValues);
                    i++;
                }

                // finishing assignment
                stable = model.finishAssign();
            }
            // assessing quality of this model
            double distanceSum = 0;
            i = 0;
            for (Example example : exampleSet) {
                double distance = measure.calculateDistance(model.getCentroidCoordinates(centroidAssignments[i]),
                        getAsDoubleArray(example, attributes, values));
                distanceSum += distance * distance;
                i++;
            }
            if (distanceSum < minimalIntraClusterDistance) {
                bestModel = model;
                minimalIntraClusterDistance = distanceSum;
                bestAssignments = centroidAssignments;
            }
            getProgress().setCompleted((iter + 1) * maxOptimizationSteps);
        }
        bestModel.setClusterAssignments(bestAssignments, exampleSet);

        if (addsClusterAttribute()) {
            Attribute cluster;
            if (!getParameterAsBoolean(PARAMETER_ADD_AS_LABEL)) {
                cluster = AttributeFactory.createAttribute(Attributes.CLUSTER_NAME, Ontology.NOMINAL);
                exampleSet.getExampleTable().addAttribute(cluster);
                attributes.setCluster(cluster);
            } else {
                cluster = AttributeFactory.createAttribute(Attributes.LABEL_NAME, Ontology.NOMINAL);
                exampleSet.getExampleTable().addAttribute(cluster);
                attributes.setLabel(cluster);
            }

            int i = 0;
            for (Example example : exampleSet) {
                example.setValue(cluster, "cluster_" + bestAssignments[i]);
                i++;
            }
        }

        getProgress().complete();

        logger.info("KMeans generate cluster model finished.");

        return bestModel;
    }

    private double[] getAsDoubleArray(Example example, Attributes attributes, double[] values) {
        int i = 0;
        for (Attribute attribute : attributes) {
            values[i] = example.getValue(attribute);
            i++;
        }
        return values;
    }

    @Override
    public Class<? extends ClusterModel> getClusterModelClass() {
        return CentroidClusterModel.class;
    }

    @Override
    public boolean supportsCapability(OperatorCapability capability) {
        boolean supportsNominal = false;
        boolean pureNominal = false;
        try {
            // Check if a measure type is selected that supports nominal attributes
            int selectedMeasureType = measureHelper.getSelectedMeasureType();
            pureNominal = selectedMeasureType == DistanceMeasures.NOMINAL_MEASURES_TYPE;
            supportsNominal = selectedMeasureType == DistanceMeasures.MIXED_MEASURES_TYPE || pureNominal;
        } catch (UndefinedParameterError e) {
            // parameter is undefined we will stick tell that we do not support nominal attributes
        }
        switch (capability) {
            case NUMERICAL_ATTRIBUTES:
                return !pureNominal;
            case BINOMINAL_ATTRIBUTES:
            case POLYNOMINAL_ATTRIBUTES:
                return supportsNominal;
            default:
                return true;
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeInt(PARAMETER_K, I18N.getMessage("pio.KMeans.k"), 2,
                Integer.MAX_VALUE, 2, false));
        types.add(new ParameterTypeInt(PARAMETER_MAX_RUNS,
                I18N.getMessage("pio.KMeans.max_runs"), 1,
                Integer.MAX_VALUE, 10, false));

        ParameterType type = new ParameterTypeBoolean(KMeanspp.PARAMETER_USE_KPP,
                I18N.getMessage("pio.KMeanspp.determine_good_start_values"), false);
        type.setExpert(false);
        types.add(type);

        for (ParameterType a : DistanceMeasures.getParameterTypes(this)) {
            if (a.getKey() == DistanceMeasures.PARAMETER_MEASURE_TYPES) {
                a.setDefaultValue(DistanceMeasures.DIVERGENCES_TYPE);
            }
            if (a.getKey() == DistanceMeasures.PARAMETER_DIVERGENCE) {
                a.setDefaultValue(6);
            }
            types.add(a);
        }

        types.add(new ParameterTypeInt(PARAMETER_MAX_OPTIMIZATION_STEPS,
                I18N.getMessage("pio.KMeans.max_optimization_steps"), 1, Integer.MAX_VALUE, 100, false));
        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));
        return types;
    }
}
