package io.sugo.pio.operator.preprocessing.sampling;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.MappedExampleSet;
import io.sugo.pio.example.set.SplittedExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.preprocessing.sampling.sequences.AbsoluteSamplingSequenceGenerator;
import io.sugo.pio.operator.preprocessing.sampling.sequences.ProbabilitySamplingSequenceGenerator;
import io.sugo.pio.operator.preprocessing.sampling.sequences.RelativeSamplingSequenceGenerator;
import io.sugo.pio.operator.preprocessing.sampling.sequences.SamplingSequenceGenerator;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.PortType;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import io.sugo.pio.tools.RandomGenerator;

import java.util.List;


/**
 * This operator will sample the given example set without replacement. Three modes are available:
 * Absolute returning a determined number, relative returning a determined fraction of the input set
 * and probability, that will return each example with the same probability.
 * <p>
 * The operator offers the possibility to specify sampling parameter per class to re-balance the
 * data.
 */
public class SamplingOperator extends AbstractSamplingOperator {

    private static final Logger logger = new Logger(SamplingOperator.class);

    public static final String PARAMETER_SAMPLE = "sample";

    private final OutputPort remainedExampleSetOutput = getOutputPorts().createPort(PortType.REMAINED_EXAMPLE_SET_OUTPUT);

//    public static String[] SAMPLE_MODES = {"absolute", "relative", "probability"};
    public static String[] SAMPLE_MODES = {
            I18N.getMessage("pio.SamplingOperator.sample_modes.absolute"),
            I18N.getMessage("pio.SamplingOperator.sample_modes.relative"),
            I18N.getMessage("pio.SamplingOperator.sample_modes.probability")
    };

    public static final int SAMPLE_ABSOLUTE = 0;

    public static final int SAMPLE_RELATIVE = 1;

    public static final int SAMPLE_PROBABILITY = 2;

    /**
     * The parameter name for &quot;The number of examples which should be sampled&quot;
     */
    public static final String PARAMETER_SAMPLE_SIZE = "sample_size";

    /**
     * The parameter name for &quot;The fraction of examples which should be sampled&quot;
     */
    public static final String PARAMETER_SAMPLE_RATIO = "sample_ratio";

    public static final String PARAMETER_SAMPLE_PROBABILITY = "sample_probability";

    public static final String PARAMETER_BALANCE_DATA = "balance_data";

    public static final String PARAMETER_SAMPLE_SIZE_LIST = "sample_size_per_class";

    public static final String PARAMETER_SAMPLE_RATIO_LIST = "sample_ratio_per_class";

    public static final String PARAMETER_SAMPLE_PROBABILITY_LIST = "sample_probability_per_class";

    private ExampleSet unusedExampleSet;

    public SamplingOperator() {
        super();

        ExampleSetPrecondition needNominalLabelCondition = new ExampleSetPrecondition(getExampleSetInputPort(),
                Attributes.LABEL_NAME, Ontology.NOMINAL);
        getExampleSetInputPort().addPrecondition(
                new ParameterConditionedPrecondition(getExampleSetInputPort(), needNominalLabelCondition,
                        getParameterHandler(), PARAMETER_BALANCE_DATA, Boolean.toString(true)));
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.SamplingOperator.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.SamplingOperator.description");
    }

    @Override
    public IOContainer getResult() {
        IOContainer container = super.getResult();

        List<IOObject> ioObjects = container.getIoObjects();
        ioObjects.add(remainedExampleSetOutput.getAnyDataOrNull());

        return container;
    }

    @Override
    protected MDInteger getSampledSize(ExampleSetMetaData emd) throws UndefinedParameterError {
        boolean balanceData = getParameterAsBoolean(PARAMETER_BALANCE_DATA);
        int absoluteNumber = 0;
        switch (getParameterAsInt(PARAMETER_SAMPLE)) {
            case SAMPLE_ABSOLUTE:
                if (balanceData) {
                    List<String[]> parameterList = getParameterList(PARAMETER_SAMPLE_SIZE_LIST);
                    for (String[] pair : parameterList) {
                        absoluteNumber += Integer.valueOf(pair[1]);
                    }
                } else {
                    absoluteNumber = getParameterAsInt(PARAMETER_SAMPLE_SIZE);
                }
                if (emd.getNumberOfExamples().isAtLeast(absoluteNumber) == MetaDataInfo.NO) {
                    getExampleSetInputPort().addError(
                            new SimpleMetaDataError(Severity.ERROR, getExampleSetInputPort(),
                                    "pio.error.metadata.exampleset.need_more_examples",
                                    absoluteNumber + ""));
                }
                return new MDInteger(absoluteNumber);
            case SAMPLE_RELATIVE:
                if (balanceData) {
                    MDInteger numberOfExamples = emd.getNumberOfExamples();
                    numberOfExamples.reduceByUnknownAmount();
                    return numberOfExamples;
                }
                if (emd.getNumberOfExamples().isKnown()) {
                    return new MDInteger((int) ((getParameterAsDouble(PARAMETER_SAMPLE_RATIO) * emd.getNumberOfExamples()
                            .getValue())));
                }
                return new MDInteger();
            case SAMPLE_PROBABILITY:
                if (balanceData) {
                    MDInteger numberOfExamples = emd.getNumberOfExamples();
                    numberOfExamples.reduceByUnknownAmount();
                    return numberOfExamples;
                }
                if (emd.getNumberOfExamples().isKnown()) {
                    return new MDInteger((int) ((getParameterAsDouble(PARAMETER_SAMPLE_PROBABILITY) * emd
                            .getNumberOfExamples().getValue())));
                }
                return new MDInteger();
            default:
                return new MDInteger();
        }
    }

    @Override
    public ExampleSet apply(ExampleSet originalSet) throws OperatorException {
        int resultSize = 0;
        int[] usedIndices = new int[originalSet.size()];
        int unusedResultSize = 0;
        int[] unusedIndices = new int[originalSet.size()];

        SplittedExampleSet perLabelSets = null;
        int numberOfIterations = 1; // if sampling not per class, just one iteration
        boolean balanceData = getParameterAsBoolean(PARAMETER_BALANCE_DATA);
        Attribute label = null;
        List<String[]> pairs = null;
        ExampleSet exampleSet = null;
        if (balanceData) {
            label = originalSet.getAttributes().getLabel();
            if (label != null) {
                if (label.isNominal()) {
                    perLabelSets = SplittedExampleSet.splitByAttribute(originalSet, label);
                    exampleSet = perLabelSets;
                } else {
                    throw new UserError(this, "pio.error.attribute_must_nominal", this, label.getName());
                }
            } else {
                throw new UserError(this, "pio.error.operator.exampleset_miss_label");
            }

            switch (getParameterAsInt(PARAMETER_SAMPLE)) {
                case SAMPLE_RELATIVE:
                    pairs = getParameterList(PARAMETER_SAMPLE_RATIO_LIST);
                    break;
                case SAMPLE_ABSOLUTE:
                    pairs = getParameterList(PARAMETER_SAMPLE_SIZE_LIST);
                    break;
                case SAMPLE_PROBABILITY:
                default:
                    pairs = getParameterList(PARAMETER_SAMPLE_PROBABILITY_LIST);
            }
            numberOfIterations = pairs.size();

            int numberOfSubsets = perLabelSets.getNumberOfSubsets();
            if (numberOfSubsets < numberOfIterations) {
                logger.warn("Cannot instantiate {0}. Skipping...",
                        new Object[]{(numberOfIterations - numberOfSubsets), numberOfIterations, numberOfSubsets});
                numberOfIterations = numberOfSubsets;
            }
        } else {
            exampleSet = originalSet;
        }

        // now iterate over all subsets
        for (int i = 0; i < numberOfIterations; i++) {
            SamplingSequenceGenerator sampleSequence = null;
            if (balanceData) {
                perLabelSets.clearSelection();
                perLabelSets.selectAdditionalSubset(i);
                String parameter = "0";

                // finding parameter list of the selected sample method
                if (exampleSet.size() > 0) {
                    // getting parameter value for current class
                    Example next = exampleSet.iterator().next();
                    String labelValue = next.getValueAsString(label);
                    for (String[] pair : pairs) {
                        if (labelValue.equals(pair[0])) {
                            parameter = pair[1];
                            break;
                        }
                    }
                }

                // now generate sampling sequence for this class's parameter value
                switch (getParameterAsInt(PARAMETER_SAMPLE)) {
                    case SAMPLE_RELATIVE:
                        sampleSequence = new RelativeSamplingSequenceGenerator(exampleSet.size(), Double.valueOf(parameter),
                                RandomGenerator.getRandomGenerator(this));
                        break;
                    case SAMPLE_ABSOLUTE:
                        sampleSequence = new AbsoluteSamplingSequenceGenerator(exampleSet.size(),
                                Integer.valueOf(parameter), RandomGenerator.getRandomGenerator(this));
                        break;
                    case SAMPLE_PROBABILITY:
                    default:
                        sampleSequence = new ProbabilitySamplingSequenceGenerator(Double.valueOf(parameter),
                                RandomGenerator.getRandomGenerator(this));
                        break;
                }

            } else {
                // just retrieve the standard parameters
                switch (getParameterAsInt(PARAMETER_SAMPLE)) {
                    case SAMPLE_RELATIVE:
                        sampleSequence = new RelativeSamplingSequenceGenerator(exampleSet.size(),
                                getParameterAsDouble(PARAMETER_SAMPLE_RATIO), RandomGenerator.getRandomGenerator(this));
                        break;
                    case SAMPLE_ABSOLUTE:
                        int size = getParameterAsInt(PARAMETER_SAMPLE_SIZE);
                        if (size > exampleSet.size()) {
                            throw new UserError(this, "pio.error.operator.exampleset_too_few", size);
                        }
                        sampleSequence = new AbsoluteSamplingSequenceGenerator(exampleSet.size(), size,
                                RandomGenerator.getRandomGenerator(this));
                        break;
                    case SAMPLE_PROBABILITY:
                    default:
                        sampleSequence = new ProbabilitySamplingSequenceGenerator(
                                getParameterAsDouble(PARAMETER_SAMPLE_PROBABILITY), RandomGenerator.getRandomGenerator(this));
                        break;
                }
            }

            // add indices which are used
            for (int j = 0; j < exampleSet.size(); j++) {
                if (sampleSequence.useNext()) {
                    if (balanceData) {
                        usedIndices[resultSize] = perLabelSets.getActualParentIndex(j);
                    } else {
                        usedIndices[resultSize] = j;
                    }
                    resultSize++;
                } else {
                    unusedIndices[unusedResultSize++] = j;
                }

            }
        }

        // create new filtered example set
        int[] resultIndices = new int[resultSize];
        System.arraycopy(usedIndices, 0, resultIndices, 0, resultSize);

        // create new filtered unused example set
        int[] unusedResultIndices = new int[unusedResultSize];
        System.arraycopy(unusedIndices, 0, unusedResultIndices, 0, unusedResultSize);
        unusedExampleSet = new MappedExampleSet(originalSet, unusedResultIndices, true, true);

        return new MappedExampleSet(originalSet, resultIndices, true, true);
    }

    @Override
    public void deliverRemainExampleSet() {
        remainedExampleSetOutput.deliver(unusedExampleSet);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeCategory(PARAMETER_SAMPLE, I18N.getMessage("pio.SamplingOperator.sample"),
                SAMPLE_MODES, SAMPLE_ABSOLUTE);
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeBoolean(PARAMETER_BALANCE_DATA,
                I18N.getMessage("pio.SamplingOperator.balance_data"), false, true);
        type.setHidden(true);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_SAMPLE_SIZE, I18N.getMessage("pio.SamplingOperator.sample_size"), 1,
                Integer.MAX_VALUE, 100);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_ABSOLUTE));
//        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, false));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_SAMPLE_RATIO, I18N.getMessage("pio.SamplingOperator.sample_ratio"), 0.0d,
                1.0d, 0.1d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_RELATIVE));
//        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, false));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_SAMPLE_PROBABILITY, I18N.getMessage("pio.SamplingOperator.sample_probability"), 0.0d, 1.0d,
                0.1d);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true,
                SAMPLE_PROBABILITY));
//        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, false));
        type.setExpert(false);
        types.add(type);

       /* type = new ParameterTypeList(PARAMETER_SAMPLE_SIZE_LIST, I18N.getMessage("pio.SamplingOperator.sample_size_per_class"),
                new ParameterTypeString("class", I18N.getMessage("pio.SamplingOperator.class")),
                new ParameterTypeInt("size", I18N.getMessage("pio.SamplingOperator.size"), 0, Integer.MAX_VALUE));
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_ABSOLUTE));
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, true));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeList(PARAMETER_SAMPLE_RATIO_LIST, I18N.getMessage("pio.SamplingOperator.sample_ratio_per_class"),
                new ParameterTypeString("class", I18N.getMessage("pio.SamplingOperator.class")),
                new ParameterTypeDouble("ratio",I18N.getMessage("pio.SamplingOperator.ratio"), 0, 1));
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true, SAMPLE_RELATIVE));
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, true));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeList(PARAMETER_SAMPLE_PROBABILITY_LIST, I18N.getMessage("pio.SamplingOperator.sample_probability_per_class"),
                new ParameterTypeString("class", I18N.getMessage("pio.SamplingOperator.class")),
                new ParameterTypeDouble("probability", I18N.getMessage("pio.SamplingOperator.probability"), 0, 1));
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_SAMPLE, SAMPLE_MODES, true,
                SAMPLE_PROBABILITY));
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_BALANCE_DATA, true, true));
        type.setExpert(false);
        types.add(type);

        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));*/
        return types;
    }

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), SamplingOperator.class,
                null);
    }
}
