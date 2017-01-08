package io.sugo.pio.operator.io.csv;

import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.desc.Description;
import io.sugo.pio.operator.desc.InputPortDesc;
import io.sugo.pio.operator.desc.ParamType;
import io.sugo.pio.operator.desc.ParameterDesc;

import java.util.ArrayList;
import java.util.List;

public class CSVWriterMeta extends OperatorMeta {
    public static final String TYPE = "csv_writer";
    @Override
    protected List<Description> getOperatorParameters() {
        List<Description> params = new ArrayList<>();
        params.add(ParameterDesc.create("writeTime", "writeTime description", ParamType.INTEGER, "5"));
        params.add(InputPortDesc.create("inputPort"));
        return params;
    }

    @Override
    public String getOperatorType() {
        return TYPE;
    }
}
