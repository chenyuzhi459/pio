package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "descType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "primitive", value = ParameterDesc.class),
        @JsonSubTypes.Type(name = "list", value = ListParameterDesc.class),
        @JsonSubTypes.Type(name = "map", value = MapParameterDesc.class),
        @JsonSubTypes.Type(name = "custom", value = CustomParameterDesc.class)
})
public interface Description extends Serializable {
}
