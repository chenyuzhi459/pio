package io.sugo.pio.operator.io.csv;

import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.desc.*;

import java.util.ArrayList;
import java.util.List;

public class CSVSpliterMeta extends OperatorMeta {
    public static final String TYPE = "csv_spliter";
    @Override
    protected List<Description> getOperatorParameters() {
        List<Description> params = new ArrayList<>();
        params.add(ParameterDesc.create("writeTime", "writeTime description", ParamType.INTEGER, "5"));
        ListParameterDesc inPorts = ListParameterDesc.create("inputPorts", "", InputPortDesc.create("", "csv_spliter_input"));
        params.add(inPorts);
        ListParameterDesc outPorts = ListParameterDesc.create("outputPorts", "", InputPortDesc.create("", "csv_spliter_output"));
        params.add(outPorts);
        params.add(InputPortDesc.create("inputPort", ""));
        params.add(OutputPortDesc.create("outputPort", ""));
        return params;
    }

    @Override
    public String getOperatorType() {
        return TYPE;
    }
}
