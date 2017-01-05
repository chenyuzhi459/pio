package io.sugo.pio.operator.io.csv;

import io.sugo.pio.operator.OperatorMeta;
import io.sugo.pio.operator.desc.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-1-5.
 */
public class CSVReaderTestMeta extends OperatorMeta {
    public static final String TYPE = "csv_reader_test";

    @Override
    protected List<Description> getOperatorParameters() {
        List<Description> params = new ArrayList<>();
        params.add(ParameterDesc.create("readTime", "readTime description", ParamType.INTEGER, "5"));
        params.add(ParameterDesc.create("tableName", "tableName description", ParamType.STRING, "table_test"));
        params.add(ParameterDesc.create("score", "score description", ParamType.DOUBLE, "95.5"));
        params.add(ListParameterDesc.create("data", "data description", ParameterDesc.createString("", "")));
        params.add(MapParameterDesc.create("map", "map description", ParamType.INTEGER, ParamType.STRING));

        CustomParameterDesc desc = CustomParameterDesc.create("person", "data description");
        desc.addParameterDesc(ParameterDesc.createString("name", "person name"));
        desc.addParameterDesc(ParameterDesc.createInt("age", "person age"));
        desc.addParameterDesc(ListParameterDesc.create("scores", "person scores", ParameterDesc.createDouble("", "")));
        params.add(desc);

        return params;
    }

    @Override
    public String getOperatorType() {
        return TYPE;
    }
}
