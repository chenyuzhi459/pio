package io.sugo.pio.operator.preprocessing.filter;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import io.sugo.pio.tools.Tools;


/**
 * Converts all numerical attributes to nominal ones. Each numerical value is simply used as nominal
 * value of the new attribute. If the value is missing, the new value will be missing. Please note
 * that this operator might drastically increase memory usage if many different numerical values are
 * used. Please use the available discretization operators then.
 */
public class NumericToPolynominal extends NumericToNominal {

    public NumericToPolynominal() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.NumericToPolynominal.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.NumericToPolynominal.description");
    }

    @Override
    protected void setValue(Example example, Attribute newAttribute, double value) {
        if (Double.isNaN(value)) {
            example.setValue(newAttribute, Double.NaN);
        } else {
            example.setValue(newAttribute, newAttribute.getMapping().mapString(Tools.formatIntegerIfPossible(value)));
        }
    }

    @Override
    protected int getGeneratedAttributevalueType() {
        return Ontology.NOMINAL;
    }

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), NumericToPolynominal.class,
                null);
    }
}
