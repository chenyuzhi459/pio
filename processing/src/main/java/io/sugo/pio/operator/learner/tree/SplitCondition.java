package io.sugo.pio.operator.learner.tree;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.example.Example;

import java.io.Serializable;

/**
 * A condition for a split in decision tree, rules etc. Subclasses should also implement a toString
 * method.
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "splitType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "greater", value = GreaterSplitCondition.class),
        @JsonSubTypes.Type(name = "less_equals", value = LessEqualsSplitCondition.class),
        @JsonSubTypes.Type(name = "contain", value = ContainsSplitCondition.class),
        @JsonSubTypes.Type(name = "not_contain", value = NotContainsSplitCondition.class),
        @JsonSubTypes.Type(name = "nominal", value = NominalSplitCondition.class),
        @JsonSubTypes.Type(name = "numerical_missing", value = NumericalMissingSplitCondition.class),
})
public interface SplitCondition extends Serializable {

    public String getAttributeName();

    public String getRelation();

    public String getValueString();

    public boolean test(Example example);

}
