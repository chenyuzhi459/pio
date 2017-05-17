package io.sugo.pio.operator.preprocessing.normalization;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.operator.preprocessing.PreprocessingModel;
import io.sugo.pio.operator.preprocessing.PreprocessingOperator;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This operator performs a normalization. This can be done between a user defined minimum and
 * maximum value or by a z-transformation, i.e. on mean 0 and variance 1. or by a proportional
 * transformation as proportion of the total sum of the respective attribute.
 *
 */
public class Normalization extends PreprocessingOperator {

    private static final Logger logger = new Logger(Normalization.class);

    private static final String PARAMETER_ATTRIBUTES = "attributes";

    private static final ArrayList<NormalizationMethod> METHODS = new ArrayList<NormalizationMethod>();

    static {
        registerNormalizationMethod(new ZTransformationNormalizationMethod());
        registerNormalizationMethod(new RangeNormalizationMethod());
//        registerNormalizationMethod(new ProportionNormalizationMethod());
//        registerNormalizationMethod(new IQRNormalizationMethod());
    }

    /**
     * This must not be modified outside this class!
     */
    public static String[] NORMALIZATION_METHODS;

    public static final int METHOD_Z_TRANSFORMATION = 0;

    public static final int METHOD_RANGE_TRANSFORMATION = 1;

    public static final int METHOD_PROPORTION_TRANSFORMATION = 2;

    public static final String PARAMETER_NORMALIZATION_METHOD = "method";

    /**
     * Incompatible version, old version writes into the exampleset, if original output port is not
     * connected.
     */
    private static final OperatorVersion VERSION_MAY_WRITE_INTO_DATA = new OperatorVersion(7, 1, 1);

    /**
     * Creates a new Normalization operator.
     */
    public Normalization() {
        super();
    }

    @Override
    protected Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd)
            throws UndefinedParameterError {
        if (amd.isNumerical()) {
            amd.setType(Ontology.REAL);
            int method = getParameterAsInt(PARAMETER_NORMALIZATION_METHOD);
            NormalizationMethod normalizationMethod = METHODS.get(method);
            return normalizationMethod.modifyAttributeMetaData(emd, amd, getExampleSetInputPort(), this);
        }
        return Collections.singleton(amd);
    }

    @Override
    public PreprocessingModel createPreprocessingModel(ExampleSet exampleSet) throws OperatorException {
        logger.info("Normalization begin to create preprocessing model through example set[%s]...", exampleSet.getName());

        int method = getParameterAsInt(PARAMETER_NORMALIZATION_METHOD);
        NormalizationMethod normalizationMethod = METHODS.get(method);
        normalizationMethod.init();
        collectLog("Normalization with method: " + normalizationMethod.getDisplayName());

        return normalizationMethod.getNormalizationModel(exampleSet, this);
    }

    @Override
    public Class<? extends PreprocessingModel> getPreprocessingModelClass() {
        return AbstractNormalizationModel.class;
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.Normalization.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.normalization;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.Normalization.description");
    }

    @Override
    public int getSequence() {
        return 0;
    }

    /**
     * Returns a list with all parameter types of this model.
     */
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        types.add(new ParameterTypeCategory(PARAMETER_NORMALIZATION_METHOD, I18N.getMessage("pio.Normalization.method"),
                NORMALIZATION_METHODS, 0));
        int i = 0;
        for (NormalizationMethod method : METHODS) {
            for (ParameterType type : method.getParameterTypes(this)) {
                type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_NORMALIZATION_METHOD,
                        NORMALIZATION_METHODS, true, new int[]{i}));
                types.add(type);
            }
            i++;
        }

        types.forEach(parameterType -> {
            if (PARAMETER_ATTRIBUTES.equals(parameterType.getKey())) {
                parameterType.setDescription(I18N.getMessage("pio.Normalization.attributes_desc"));
                parameterType.setFullName(I18N.getMessage("pio.Normalization.attributes_desc"));
            }
        });
        return types;
    }

    @Override
    protected int[] getFilterValueTypes() {
        return new int[]{Ontology.NUMERICAL};
    }

    @Override
    public boolean writesIntoExistingData() {
//		if (getCompatibilityLevel().isAbove(VERSION_MAY_WRITE_INTO_DATA)) {
        return super.writesIntoExistingData();
//		} else {
//			// old version: true only if original output port is connected
//			return isOriginalOutputConnected() && super.writesIntoExistingData();
//		}
    }

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), Normalization.class,
                attributeSelector);
    }

	/*@Override
    public OperatorVersion[] getIncompatibleVersionChanges() {
		return (OperatorVersion[]) ArrayUtils.addAll(super.getIncompatibleVersionChanges(),
				new OperatorVersion[] { VERSION_MAY_WRITE_INTO_DATA });
	}*/

    /**
     * This method can be used for registering additional normalization methods.
     */
    public static void registerNormalizationMethod(NormalizationMethod newMethod) {
        METHODS.add(newMethod);
        NORMALIZATION_METHODS = new String[METHODS.size()];
        int i = 0;
        for (NormalizationMethod method : METHODS) {
//            NORMALIZATION_METHODS[i] = method.getName();
            NORMALIZATION_METHODS[i] = method.getDisplayName();
            i++;
        }
    }

}
