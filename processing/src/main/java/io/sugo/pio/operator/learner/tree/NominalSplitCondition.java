package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.tools.Tools;

/**
 * A split condition for nominal values (equals).
 */
public class NominalSplitCondition extends AbstractSplitCondition {

    private static final long serialVersionUID = 3883155435836330171L;

    @JsonProperty
    private double value;

    @JsonProperty
    private String valueString;

    public NominalSplitCondition(Attribute attribute, String valueString) {
        super(attribute.getName());
        if (valueString == null) {
            this.value = Double.NaN;
            this.valueString = Attribute.MISSING_NOMINAL_VALUE;
        } else {
            this.value = attribute.getMapping().getIndex(valueString);
            this.valueString = valueString;
        }
    }

    @Override
    public boolean test(Example example) {
        double currentValue = example.getValue(example.getAttributes().get(getAttributeName()));
        return Tools.isEqual(currentValue, value);
    }

    @Override
    public String getRelation() {
        return "=";
    }

    @Override
    public String getValueString() {
        return this.valueString;
    }
}
