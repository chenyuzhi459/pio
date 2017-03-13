package io.sugo.pio.operator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.set.ConditionedExampleSet;
import io.sugo.pio.example.set.MappedExampleSet;
import io.sugo.pio.operator.clustering.CentroidClusterModel;
import io.sugo.pio.operator.learner.associations.AssociationRules;
import io.sugo.pio.operator.learner.associations.FrequentItemSets;
import io.sugo.pio.operator.learner.functions.LinearRegressionModel;
import io.sugo.pio.operator.learner.functions.kernel.JMySVMModel;
import io.sugo.pio.operator.learner.tree.ConfigurableRandomForestModel;
import io.sugo.pio.operator.learner.tree.TreeModel;
import io.sugo.pio.operator.performance.*;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.tools.math.Averagable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


/**
 * This interface must be implemented by all objects that can be input/output objects for Operators.
 * The copy method is necessary in cases where meta operator chains want to copy the input
 * IOContainer before it is given to the children operators. Please note that the method only need
 * to be implemented like a usual <code>clone</code> method for IO objects which can be altered
 * after creation. In all other cases the implementation can simply return the same object. Hence,
 * we use the name <code>copy</code> instead of <code>clone</code>.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "dataType")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "tree_model", value = TreeModel.class),
        @JsonSubTypes.Type(name = "linear_regression", value = LinearRegressionModel.class),
        @JsonSubTypes.Type(name = "svm", value = JMySVMModel.class),
        @JsonSubTypes.Type(name = "random_forest", value = ConfigurableRandomForestModel.class),
        @JsonSubTypes.Type(name = "centroid_cluster", value = CentroidClusterModel.class),
        @JsonSubTypes.Type(name = "performance_vector", value = PerformanceVector.class),
        @JsonSubTypes.Type(name = "attribute_weights", value = AttributeWeights.class),
        @JsonSubTypes.Type(name = "conditioned_example_set", value = ConditionedExampleSet.class),
        @JsonSubTypes.Type(name = "multi_class_performance", value = MultiClassificationPerformance.class),
        @JsonSubTypes.Type(name = "weight_multi_class_performance", value = WeightedMultiClassPerformance.class),
        @JsonSubTypes.Type(name = "binary_class_performance", value = BinaryClassificationPerformance.class),
//        @JsonSubTypes.Type(name = "averagable", value = Averagable.class),
        @JsonSubTypes.Type(name = "mapped_example_set", value = MappedExampleSet.class),
        @JsonSubTypes.Type(name = "frequent_item_sets", value = FrequentItemSets.class),
        @JsonSubTypes.Type(name = "association_rules", value = AssociationRules.class),
        @JsonSubTypes.Type(name = "absolute_error", value = AbsoluteError.class),
        @JsonSubTypes.Type(name = "rank_correlation", value = RankCorrelation.class)

})
public interface IOObject extends Serializable {

    /**
     * Sets the source of this IOObject.
     */
    public void setSource(String sourceName);

    /**
     * Returns the source of this IOObject (might return null if the source is unknown).
     */
    public String getSource();

    /**
     * This method is called if this IOObject is put out of the specified port after being created
     * or processed by the given operator. This method has to keep track of the processing stations
     * of this object, so that they are remembered and might be returned by the method
     * getProcessingHistory.
     */
    public void appendOperatorToHistory(Operator operator, OutputPort port);

    /**
     * Should return a copy of this IOObject. Please note that the method can usually be implemented
     * by simply returning the same object (i.e. return this;). The object needs only to be cloned
     * in cases the IOObject can be altered after creation. This is for example the case for
     * ExampleSets.
     */
    public IOObject copy();

    /**
     * Writes the object data into a stream.
     */
    public void write(OutputStream out) throws IOException;

    public Annotations getAnnotations();

    /**
     * Returns user specified data attached to the IOObject. The key has to be a fully qualified
     * name
     */
    public Object getUserData(String key);

    /**
     * Specify user data attached to the IOObject. The key has to be a fully qualified name
     *
     * @return the previous value associated with key, or null if there was no mapping for key. (A
     * null return can also indicate that the user data previously associated null with
     * key.)
     */
    public Object setUserData(String key, Object value);
}
