package io.sugo.pio.operator.preprocessing.filter;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.SetRelation;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import io.sugo.pio.tools.Range;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Converts all numerical attributes to binary ones. If the value of an attribute is between the
 * specified minimal and maximal value, it becomes <em>false</em>, otherwise <em>true</em>. If the
 * value is missing, the new value will be missing. The default boundaries are both set to 0, thus
 * only 0.0 is mapped to false and all other values are mapped to true.
 */
public class NumericToBinominal extends NumericToNominal {

    /**
     * The parameter name for &quot;The minimal value which is mapped to false (included).&quot;
     */
    public static final String PARAMETER_MIN = "min";

    /**
     * The parameter name for &quot;The maximal value which is mapped to false (included).&quot;
     */
    public static final String PARAMETER_MAX = "max";

    private static final String PARAMETER_ATTRIBUTES = "attributes";

    public NumericToBinominal() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.NumericToBinominal.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.fieldSetting;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.NumericToBinominal.description");
    }

    @Override
    public int getSequence() {
        return 2;
    }

    @Override
    public ExampleSetMetaData applyOnFilteredMetaData(ExampleSetMetaData emd) throws UndefinedParameterError {
        double min = getParameterAsDouble(PARAMETER_MIN);
        double max = getParameterAsDouble(PARAMETER_MAX);

        for (AttributeMetaData amd : emd.getAllAttributes()) {
            if (amd.isNumerical()) {
                Range valueRange = amd.getValueRange();
                amd.setType(Ontology.BINOMINAL);
                // all values below min?
                if (amd.getValueSetRelation() != SetRelation.SUPERSET && valueRange.getUpper() < min
                        || valueRange.getLower() > max) {
                    amd.setValueSet(Collections.singleton("true"), SetRelation.EQUAL);
                    continue;
                }
                // all values above max?
                if (amd.getValueSetRelation() != SetRelation.SUPERSET && valueRange.getLower() > min
                        && valueRange.getUpper() < max) {
                    amd.setValueSet(Collections.singleton("false"), SetRelation.EQUAL);
                    continue;
                }
                Set<String> values = new TreeSet<String>();
                values.add("false");
                values.add("true");
                amd.setValueSet(values, SetRelation.SUBSET);
            }
        }
        return emd;
    }

    @Override
    protected void setValue(Example example, Attribute newAttribute, double value) throws OperatorException {
        double min = getParameterAsDouble(PARAMETER_MIN);
        double max = getParameterAsDouble(PARAMETER_MAX);
        if (Double.isNaN(value)) {
            example.setValue(newAttribute, Double.NaN);
        } else if ((value < min) || (value > max)) {
            example.setValue(newAttribute, newAttribute.getMapping().mapString("true"));
        } else {
            example.setValue(newAttribute, newAttribute.getMapping().mapString("false"));
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterTypeDouble minType = new ParameterTypeDouble(PARAMETER_MIN, I18N.getMessage("pio.NumericToBinominal.min"),
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0d);
        minType.setHidden(true);
        types.add(minType);

        ParameterTypeDouble maxType = new ParameterTypeDouble(PARAMETER_MAX, I18N.getMessage("pio.NumericToBinominal.max"),
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0d);
        maxType.setHidden(true);
        types.add(maxType);

        types.forEach(parameterType -> {
            if (PARAMETER_ATTRIBUTES.equals(parameterType.getKey())) {
                parameterType.setDescription(I18N.getMessage("pio.NumericToBinominal.attributes_desc"));
                parameterType.setFullName(I18N.getMessage("pio.NumericToBinominal.attributes_desc"));
            }
        });

        return types;
    }

    @Override
    protected int getGeneratedAttributevalueType() {
        return Ontology.BINOMINAL;
    }

    @Override
    protected Attribute makeAttribute() {
        Attribute att = AttributeFactory.createAttribute(getGeneratedAttributevalueType());
        att.getMapping().mapString("false");
        att.getMapping().mapString("true");
        return att;
    }

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), NumericToBinominal.class,
                null);
    }
}
