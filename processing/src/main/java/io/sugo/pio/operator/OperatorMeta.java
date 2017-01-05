package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.sugo.pio.operator.desc.Description;
import io.sugo.pio.operator.desc.ParamType;
import io.sugo.pio.operator.desc.ParameterDesc;
import io.sugo.pio.operator.io.csv.*;

import java.io.Serializable;
import java.util.List;

@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = CSVReaderMeta.TYPE, value = CSVReaderMeta.class),
        @JsonSubTypes.Type(name = CSVModifierMeta.TYPE, value = CSVModifierMeta.class),
        @JsonSubTypes.Type(name = CSVSpliterMeta.TYPE, value = CSVSpliterMeta.class),
        @JsonSubTypes.Type(name = CSVWriterMeta.TYPE, value = CSVWriterMeta.class),
        @JsonSubTypes.Type(name = CSVReaderTestMeta.TYPE, value = CSVReaderTestMeta.class)
})
public abstract class OperatorMeta implements Serializable {
    public static final String OPERATOR_TYPE = "operatorType";

    @JsonProperty("params")
    public List<Description> getParams() {
        List<Description> params = getOperatorParameters();
        params.add(getName());
        return params;
    }

    public ParameterDesc getName() {
        return new ParameterDesc("name", "name description", ParamType.STRING, getOperatorType());
    }

    protected abstract List<Description> getOperatorParameters();

    @JsonProperty
    public abstract String getOperatorType();
}
