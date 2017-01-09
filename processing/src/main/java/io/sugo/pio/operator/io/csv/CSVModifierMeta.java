package io.sugo.pio.operator.io.csv;

import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.desc.*;

import java.util.ArrayList;
import java.util.List;

public class CSVModifierMeta extends OperatorMeta {
    public static final String TYPE = "csv_modifier";
    @Override
    protected List<Description> getOperatorParameters() {
        List<Description> params = new ArrayList<>();
        params.add(ParameterDesc.create("modifyTime", "modifyTime description", ParamType.INTEGER, "5"));
        params.add(InputPortDesc.create("inputPort"));
        params.add(OutputPortDesc.create("outputPort"));
        return params;
    }

    @Override
    public String getOperatorType() {
        return TYPE;
    }
}
