package io.sugo.pio.operator.io.csv;

import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.desc.*;

import java.util.ArrayList;
import java.util.List;

public class CSVReaderMeta extends OperatorMeta {
    public static final String TYPE = "csv_reader";
    @Override
    protected List<Description> getOperatorParameters() {
        List<Description> params = new ArrayList<>();
        params.add(ParameterDesc.create("readTime", "readTime description", ParamType.INTEGER, "5"));
        params.add(OutputPortDesc.create("outputPort"));
        return params;
    }

    @Override
    public String getOperatorType() {
        return TYPE;
    }
}
