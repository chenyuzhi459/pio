package io.sugo.pio.tools.math.similarity;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.IOContainer;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.tools.math.kernels.Kernel;
import io.sugo.pio.tools.math.similarity.divergences.*;
import io.sugo.pio.tools.math.similarity.mixed.MixedEuclideanDistance;
import io.sugo.pio.tools.math.similarity.nominal.*;
import io.sugo.pio.tools.math.similarity.numerical.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * This is a convenient class for using the distanceMeasures. It offers methods for integrating the
 * measure classes into operators.
 */
public class DistanceMeasures {

    public static final String PARAMETER_MEASURE_TYPES = "measure_types";
    public static final String PARAMETER_NOMINAL_MEASURE = "nominal_measure";
    public static final String PARAMETER_NUMERICAL_MEASURE = "numerical_measure";
    public static final String PARAMETER_MIXED_MEASURE = "mixed_measure";
    public static final String PARAMETER_DIVERGENCE = "divergence";

    /*public static final String[] MEASURE_TYPES = new String[]{"MixedMeasures", "NominalMeasures", "NumericalMeasures",
            "BregmanDivergences"};*/
    public static final String[] MEASURE_TYPES = new String[]{
            I18N.getMessage("pio.DistanceMeasures.mixed_measure"),
            I18N.getMessage("pio.DistanceMeasures.nominal_measure"),
            I18N.getMessage("pio.DistanceMeasures.numerical_measure"),
            I18N.getMessage("pio.DistanceMeasures.divergence")
    };

    public static final int MIXED_MEASURES_TYPE = 0;
    public static final int NOMINAL_MEASURES_TYPE = 1;
    public static final int NUMERICAL_MEASURES_TYPE = 2;
    public static final int DIVERGENCES_TYPE = 3;

    /*private static String[] NOMINAL_MEASURES = new String[]{"NominalDistance", "DiceSimilarity", "JaccardSimilarity",
            "KulczynskiSimilarity", "RogersTanimotoSimilarity", "RussellRaoSimilarity", "SimpleMatchingSimilarity"};*/
    private static String[] NOMINAL_MEASURES = new String[]{
            "NominalDistance",
            "DiceSimilarity",
            "JaccardSimilarity",
            "KulczynskiSimilarity",
            "RogersTanimotoSimilarity",
            "RussellRaoSimilarity",
            "SimpleMatchingSimilarity"
    };
    private static Class[] NOMINAL_MEASURE_CLASSES = new Class[]{NominalDistance.class, DiceNominalSimilarity.class,
            JaccardNominalSimilarity.class, KulczynskiNominalSimilarity.class, RogersTanimotoNominalSimilarity.class,
            RussellRaoNominalSimilarity.class, SimpleMatchingNominalSimilarity.class};

    /*private static String[] MIXED_MEASURES = new String[]{"MixedEuclideanDistance"};*/
    private static String[] MIXED_MEASURES = new String[]{I18N.getMessage("pio.DistanceMeasures.MixedEuclideanDistance")};
    private static Class[] MIXED_MEASURE_CLASSES = new Class[]{MixedEuclideanDistance.class};

    /* If this changes, the parameter dependencies might need to be updated */
    private static String[] NUMERICAL_MEASURES = new String[]{"EuclideanDistance", "CamberraDistance",
            "ChebychevDistance", "CorrelationSimilarity", "CosineSimilarity", "DiceSimilarity",
            "DynamicTimeWarpingDistance", "InnerProductSimilarity", "JaccardSimilarity", "KernelEuclideanDistance",
            "ManhattanDistance", "MaxProductSimilarity", "OverlapSimilarity"};
    private static Class[] NUMERICAL_MEASURE_CLASSES = new Class[]{EuclideanDistance.class,
            CamberraNumericalDistance.class, ChebychevNumericalDistance.class, CorrelationSimilarity.class,
            CosineSimilarity.class, DiceNumericalSimilarity.class, DTWDistance.class, InnerProductSimilarity.class,
            JaccardNumericalSimilarity.class, KernelEuclideanDistance.class, ManhattanDistance.class,
            MaxProductSimilarity.class, OverlapNumericalSimilarity.class};

    private static String[] DIVERGENCES = new String[]{"GeneralizedIDivergence", "ItakuraSaitoDistance", "KLDivergence",
            "LogarithmicLoss", "LogisticLoss", "MahalanobisDistance", "SquaredEuclideanDistance", "SquaredLoss",};
    private static Class[] DIVERGENCE_CLASSES = new Class[]{GeneralizedIDivergence.class, ItakuraSaitoDistance.class,
            KLDivergence.class, LogarithmicLoss.class, LogisticLoss.class, MahalanobisDistance.class,
            SquaredEuclideanDistance.class, SquaredLoss.class,};

    private static String[][] MEASURE_ARRAYS = new String[][]{MIXED_MEASURES, NOMINAL_MEASURES, NUMERICAL_MEASURES,
            DIVERGENCES};

    private static Class[][] MEASURE_CLASS_ARRAYS = new Class[][]{MIXED_MEASURE_CLASSES, NOMINAL_MEASURE_CLASSES,
            NUMERICAL_MEASURE_CLASSES, DIVERGENCE_CLASSES};

    /**
     * This method allows registering distance or similarity measures defined in plugins. There are
     * four different types of measures: Mixed Measures coping with examples containing nominal and
     * numerical values. Numerical and Nominal Measures work only on their respective type of
     * attribute. Divergences are a less restricted mathematical concept than distances but might be
     * used for some algorithms not needing this restrictions. This type has to be specified using
     * the first parameter.
     *
     * @param measureType  The type is available as static property of class
     * @param measureName  The name of the measure to register
     * @param measureClass The class of the measure, which needs to extend DistanceMeasure
     */
    public static void registerMeasure(int measureType, String measureName, Class<? extends DistanceMeasure> measureClass) {
        String[] newTypeNames = new String[MEASURE_ARRAYS[measureType].length + 1];
        System.arraycopy(MEASURE_ARRAYS[measureType], 0, newTypeNames, 0, MEASURE_ARRAYS[measureType].length);
        newTypeNames[newTypeNames.length - 1] = measureName;
        MEASURE_ARRAYS[measureType] = newTypeNames;

        Class[] newTypeClasses = new Class[MEASURE_CLASS_ARRAYS[measureType].length + 1];
        System.arraycopy(MEASURE_CLASS_ARRAYS[measureType], 0, newTypeClasses, 0, MEASURE_CLASS_ARRAYS[measureType].length);
        newTypeClasses[newTypeClasses.length - 1] = measureClass;
        MEASURE_CLASS_ARRAYS[measureType] = newTypeClasses;
    }

    /**
     * Creates an uninitialized distance measure. Initialize the distance measure by calling
     * {@link DistanceMeasure#init(ExampleSet, ParameterHandler)}.
     */
    public static DistanceMeasure createMeasure(ParameterHandler parameterHandler) throws UndefinedParameterError,
            OperatorException {
        return createMeasure(parameterHandler, null, null);
    }

    /**
     * @deprecated ioContainer is not used. Use a {@link DistanceMeasureHelper} to obtain distance
     * measures.
     */
    @Deprecated
    public static DistanceMeasure createMeasure(ParameterHandler parameterHandler, ExampleSet exampleSet,
                                                IOContainer ioContainer) throws UndefinedParameterError, OperatorException {
        int measureType;
        if (parameterHandler.isParameterSet(PARAMETER_MEASURE_TYPES)) {
            measureType = parameterHandler.getParameterAsInt(PARAMETER_MEASURE_TYPES);
        } else {
            // if type is not set, then might be there is no type selection: Test if one definition
            // is present
            if (parameterHandler.isParameterSet(PARAMETER_MIXED_MEASURE)) {
                measureType = MIXED_MEASURES_TYPE;
            } else if (parameterHandler.isParameterSet(PARAMETER_NOMINAL_MEASURE)) {
                measureType = NOMINAL_MEASURES_TYPE;
            } else if (parameterHandler.isParameterSet(PARAMETER_NUMERICAL_MEASURE)) {
                measureType = NUMERICAL_MEASURES_TYPE;
            } else if (parameterHandler.isParameterSet(PARAMETER_DIVERGENCE)) {
                measureType = DIVERGENCES_TYPE;
            } else {
                // if nothing fits: Try to access to get a proper exception
                measureType = parameterHandler.getParameterAsInt(PARAMETER_MEASURE_TYPES);
            }
        }
        Class[] classes = MEASURE_CLASS_ARRAYS[measureType];
        Class measureClass = null;
        switch (measureType) {
            case MIXED_MEASURES_TYPE:
                measureClass = classes[parameterHandler.getParameterAsInt(PARAMETER_MIXED_MEASURE)];
                break;
            case NOMINAL_MEASURES_TYPE:
                measureClass = classes[parameterHandler.getParameterAsInt(PARAMETER_NOMINAL_MEASURE)];
                break;
            case NUMERICAL_MEASURES_TYPE:
                measureClass = classes[parameterHandler.getParameterAsInt(PARAMETER_NUMERICAL_MEASURE)];
                break;
            case DIVERGENCES_TYPE:
                measureClass = classes[parameterHandler.getParameterAsInt(PARAMETER_DIVERGENCE)];
                break;
        }
        if (measureClass != null) {
            DistanceMeasure measure;
            try {
                measure = (DistanceMeasure) measureClass.newInstance();
                if (exampleSet != null) {
                    measure.init(exampleSet, parameterHandler);
                }
                return measure;
            } catch (InstantiationException e) {
                throw new OperatorException("Could not instanciate distance measure " + measureClass);
            } catch (IllegalAccessException e) {
                throw new OperatorException("Could not instanciate distance measure " + measureClass);
            }
        }
        return null;
    }

    public static int getSelectedMeasureType(ParameterHandler parameterHandler) throws UndefinedParameterError {
        return parameterHandler.getParameterAsInt(PARAMETER_MEASURE_TYPES);
    }

    /**
     * This method adds a parameter to chose a distance measure as parameter
     */
    public static List<ParameterType> getParameterTypes(Operator parameterHandler) {
        List<ParameterType> list = new LinkedList<ParameterType>();
        list.add(new ParameterTypeCategory(PARAMETER_MEASURE_TYPES,
                I18N.getMessage("pio.DistanceMeasures.measure_types"),
                MEASURE_TYPES, 0));
        ParameterType type = new ParameterTypeCategory(PARAMETER_MIXED_MEASURE,
                I18N.getMessage("pio.DistanceMeasures.mixed_measure"),
                MEASURE_ARRAYS[MIXED_MEASURES_TYPE], 0);
        type.registerDependencyCondition(new EqualTypeCondition(parameterHandler, PARAMETER_MEASURE_TYPES, MEASURE_TYPES,
                false, 0));
        list.add(type);
        type = new ParameterTypeCategory(PARAMETER_NOMINAL_MEASURE,
                I18N.getMessage("pio.DistanceMeasures.nominal_measure"),
                MEASURE_ARRAYS[NOMINAL_MEASURES_TYPE],
                0);
        type.registerDependencyCondition(new EqualTypeCondition(parameterHandler, PARAMETER_MEASURE_TYPES, MEASURE_TYPES,
                false, 1));
        list.add(type);
        type = new ParameterTypeCategory(PARAMETER_NUMERICAL_MEASURE,
                I18N.getMessage("pio.DistanceMeasures.numerical_measure"),
                MEASURE_ARRAYS[NUMERICAL_MEASURES_TYPE], 0);
        type.registerDependencyCondition(new EqualTypeCondition(parameterHandler, PARAMETER_MEASURE_TYPES, MEASURE_TYPES,
                false, 2));
        list.add(type);
        type = new ParameterTypeCategory(PARAMETER_DIVERGENCE,
                I18N.getMessage("pio.DistanceMeasures.divergence"),
                MEASURE_ARRAYS[DIVERGENCES_TYPE], 0);
        type.registerDependencyCondition(new EqualTypeCondition(parameterHandler, PARAMETER_MEASURE_TYPES, MEASURE_TYPES,
                false, 3));
        list.add(type);
        list.addAll(registerDependency(Kernel.getParameters(parameterHandler), 9, parameterHandler));

        return list;
    }

    /**
     * This method provides the parameters to chose only from numerical measures.
     */
    public static List<ParameterType> getParameterTypesForNumericals(ParameterHandler handler) {
        List<ParameterType> list = new LinkedList<ParameterType>();
        ParameterType type = new ParameterTypeCategory(PARAMETER_NUMERICAL_MEASURE, "Select measure",
                MEASURE_ARRAYS[NUMERICAL_MEASURES_TYPE], 0);
        list.add(type);
        return list;
    }

    private static Collection<ParameterType> registerDependency(Collection<ParameterType> sourceTypeList, int selectedValue,
                                                                Operator handler) {
        for (ParameterType type : sourceTypeList) {
            type.registerDependencyCondition(new EqualTypeCondition(handler, PARAMETER_NUMERICAL_MEASURE,
                    MEASURE_ARRAYS[NUMERICAL_MEASURES_TYPE], false, selectedValue));
            type.registerDependencyCondition(new EqualTypeCondition(handler, PARAMETER_MEASURE_TYPES, MEASURE_TYPES, false,
                    NUMERICAL_MEASURES_TYPE));
        }
        return sourceTypeList;
    }
}
