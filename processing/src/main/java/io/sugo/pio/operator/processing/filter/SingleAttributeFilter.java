package io.sugo.pio.operator.processing.filter;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeAttribute;
import io.sugo.pio.ports.InputPort;

import java.util.LinkedList;
import java.util.List;

public class SingleAttributeFilter extends AbstractAttributeFilterCondition {

    public static final String PARAMETER_ATTRIBUTE = "attribute";

    private String attributeName;

    @Override
    public void init(ParameterHandler operator) throws UserError {
        attributeName = operator.getParameterAsString(PARAMETER_ATTRIBUTE);
        if (attributeName == null || attributeName.length() == 0) {
            throw new UserError(operator instanceof Operator ? (Operator) operator : null, 111);
        }
    }

    @Override
    public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
        if (attribute.getName().equals(attributeName)) {
            return ScanResult.KEEP;
        } else {
            return ScanResult.REMOVE;
        }
    }

    @Override
    public List<ParameterType> getParameterTypes(ParameterHandler operator, InputPort inPort, int... valueTypes) {
        List<ParameterType> types = new LinkedList<>();
        ParameterType type = new ParameterTypeAttribute(PARAMETER_ATTRIBUTE, "The attribute which should be chosen.",
                inPort, true, valueTypes);
        types.add(type);
        return types;
    }
}
