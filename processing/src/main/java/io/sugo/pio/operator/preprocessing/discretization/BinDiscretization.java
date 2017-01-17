package io.sugo.pio.operator.preprocessing.discretization;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.preprocessing.PreprocessingModel;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.SetRelation;
import io.sugo.pio.tools.Ontology;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * This operator discretizes all numeric attributes in the dataset into nominal attributes. This
 * discretization is performed by simple binning, i.e. the specified number of equally sized bins is
 * created and the numerical values are simply sorted into those bins. Skips all special attributes
 * including the label.
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public class BinDiscretization extends AbstractDiscretizationOperator {

    static {
        registerDiscretizationOperator(BinDiscretization.class);
    }
    private static final String RANGE_NAME_BASE = "range";

    /** Indicates the number of used bins. */
    public static final String PARAMETER_NUMBER_OF_BINS = "number_of_bins";

    public static final String PARAMETER_DEFINE_BOUNDARIES = "define_boundaries";

    public static final String PARAMETER_MIN_VALUE = "min_value";

    public static final String PARAMETER_MAX_VALUE = "max_value";

    /** Indicates if long range names should be used. */
    public static final String PARAMETER_RANGE_NAME_TYPE = "range_name_type";

    public static final String PARAMETER_AUTOMATIC_NUMBER_OF_DIGITS = "automatic_number_of_digits";

    public static final String PARAMETER_NUMBER_OF_DIGITS = "number_of_digits";

    /**
     * Incompatible version, old version writes into the exampleset, if original output port is not
     * connected.
     */
    private static final OperatorVersion VERSION_MAY_WRITE_INTO_DATA = new OperatorVersion(7, 1, 1);

    public BinDiscretization(String name) {
        super(name);
    }

    @Override
    protected Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd)
            throws UndefinedParameterError {
        AttributeMetaData newAMD = new AttributeMetaData(amd.getName(), Ontology.NOMINAL, amd.getRole());
        Set<String> valueSet = new TreeSet<String>();
        if (getParameterAsInt(PARAMETER_RANGE_NAME_TYPE) == DiscretizationModel.RANGE_NAME_SHORT) {
            for (int i = 0; i < getParameterAsInt(PARAMETER_NUMBER_OF_BINS); i++) {
                valueSet.add(RANGE_NAME_BASE + (i + 1));
            }
            newAMD.setValueSet(valueSet, SetRelation.EQUAL);
        } else {
            newAMD.setValueSet(valueSet, SetRelation.SUPERSET);
        }
        return Collections.singletonList(newAMD);
    }

    @Override
    public PreprocessingModel createPreprocessingModel(ExampleSet exampleSet) throws OperatorException {
        DiscretizationModel model = new DiscretizationModel(exampleSet);

        exampleSet.recalculateAllAttributeStatistics();
        int numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
        HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

        if (getParameterAsBoolean(PARAMETER_DEFINE_BOUNDARIES)) {
            double min = getParameterAsDouble(PARAMETER_MIN_VALUE);
            double max = getParameterAsDouble(PARAMETER_MAX_VALUE);
            if (min > max) {
                throw new UserError(this, 116, PARAMETER_MIN_VALUE + " and " + PARAMETER_MAX_VALUE,
                        "minimum must be less than maximum");
            }
            for (Attribute attribute : exampleSet.getAttributes()) {
                if (attribute.isNumerical()) { // skip nominal and date attributes
                    double[] binRange = new double[numberOfBins + 2];
                    binRange[0] = min;
                    for (int b = 1; b < numberOfBins; b++) {
                        binRange[b] = min + (((double) b / (double) numberOfBins) * (max - min));
                    }
                    binRange[numberOfBins] = max;
                    binRange[numberOfBins + 1] = Double.POSITIVE_INFINITY;
                    ranges.put(attribute, binRange);
                }
            }
        } else {
            for (Attribute attribute : exampleSet.getAttributes()) {
                if (attribute.isNumerical()) { // skip nominal and date attributes
                    double[] binRange = new double[numberOfBins];
                    double min = exampleSet.getStatistics(attribute, Statistics.MINIMUM);
                    double max = exampleSet.getStatistics(attribute, Statistics.MAXIMUM);
                    for (int b = 0; b < numberOfBins - 1; b++) {
                        binRange[b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
                    }
                    binRange[numberOfBins - 1] = Double.POSITIVE_INFINITY;
                    ranges.put(attribute, binRange);
                }
            }
        }

        // determine number of digits
        int numberOfDigits = -1;
        if (getParameterAsBoolean(PARAMETER_AUTOMATIC_NUMBER_OF_DIGITS) == false) {
            numberOfDigits = getParameterAsInt(PARAMETER_NUMBER_OF_DIGITS);
        }

        model.setRanges(ranges, RANGE_NAME_BASE, getParameterAsInt(PARAMETER_RANGE_NAME_TYPE), numberOfDigits);
        return (model);
    }

    @Override
    public Class<? extends PreprocessingModel> getPreprocessingModelClass() {
        return DiscretizationModel.class;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_BINS,
                "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_DEFINE_BOUNDARIES, "Define the boundraries for the bin calculation.",
                false);
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_MIN_VALUE, "The minimum value for the binning range.",
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_DEFINE_BOUNDARIES, true, true));
        types.add(type);
        type = new ParameterTypeDouble(PARAMETER_MAX_VALUE, "The maximum value for the binning range.",
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_DEFINE_BOUNDARIES, true, true));
        types.add(type);

        types.add(new ParameterTypeCategory(PARAMETER_RANGE_NAME_TYPE,
                "Indicates if long range names including the limits should be used.", DiscretizationModel.RANGE_NAME_TYPES,
                DiscretizationModel.RANGE_NAME_LONG));

        type = new ParameterTypeBoolean(PARAMETER_AUTOMATIC_NUMBER_OF_DIGITS,
                "Indicates if the number of digits should be automatically determined for the range names.", true);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_RANGE_NAME_TYPE,
                DiscretizationModel.RANGE_NAME_TYPES, false, DiscretizationModel.RANGE_NAME_INTERVAL));
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_NUMBER_OF_DIGITS, "The minimum number of digits used for the interval names.",
                1, Integer.MAX_VALUE, 3);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_AUTOMATIC_NUMBER_OF_DIGITS, false,
                false));
        types.add(type);

        return types;
    }

    @Override
    public boolean writesIntoExistingData() {
        // old version: true only if original output port is connected
        return isOriginalOutputConnected() && super.writesIntoExistingData();
    }

//    @Override
//    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
//        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), BinDiscretization.class,
//                attributeSelector);
//    }
//
//    @Override
//    public OperatorVersion[] getIncompatibleVersionChanges() {
//        return (OperatorVersion[]) ArrayUtils.addAll(super.getIncompatibleVersionChanges(),
//                new OperatorVersion[] { VERSION_MAY_WRITE_INTO_DATA });
//    }
}
