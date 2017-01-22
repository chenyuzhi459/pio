package io.sugo.pio.parameter;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This attribute type supports the user by let him select an attribute name from a combo box of
 * known attribute names. For long lists, auto completion and filtering of the drop down menu eases
 * the handling. For knowing attribute names before process execution a valid meta data
 * transformation must be performed. Otherwise the user might type in the name, instead of choosing.
 */
public class ParameterTypeCompare extends ParameterTypeString {

    @JsonProperty
    private List<CompareTuple> compareOpts;
    @JsonProperty
    private List<ParameterType> innerParameters;

    private InputPort inputPort;

    public ParameterTypeCompare(final String key, String description, InputPort inputPort, List<CompareTuple> compareOpts) {
        this(key, description, inputPort, compareOpts, null);
    }

    public ParameterTypeCompare(final String key, String description, InputPort inputPort, List<CompareTuple> compareOpts, List<ParameterType> innerParameters) {
        super(key, description, "");
        this.inputPort = inputPort;
        this.compareOpts = compareOpts;
    }

    @JsonProperty
    public List<CompareTuple> getKeyList() {
        List<CompareTuple> keyList = new ArrayList<>();
        MetaData metadata = inputPort.getMetaData();
        if (metadata instanceof ExampleSetMetaData) {
            ExampleSetMetaData msMeta = (ExampleSetMetaData) metadata;
            Collection<AttributeMetaData> attrMetas = msMeta.getAllAttributes();
            for (AttributeMetaData attrMeta : attrMetas) {
                keyList.add(new CompareTuple(attrMeta.getName(), attrMeta.getValueType()));
            }
        }
        return keyList;
    }

    public List<CompareTuple> getCompareOpts() {
        return compareOpts;
    }

    public List<ParameterType> getInnerParameters() {
        return innerParameters;
    }

}
