package io.sugo.pio.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.table.ExampleTable;
import io.sugo.pio.operator.ResultObject;

/**
 */
public interface ExampleSet extends ResultObject, Cloneable, Iterable<Example> {

    /**
     * necessary since default method was added
     */
    static final long serialVersionUID = 4100925167567270064L;

    // ------------- Misc -----------------------------

    /**
     * Clones the example set.
     */
    public Object clone();

    /**
     * Frees unused resources, if supported by the implementation. Does nothing by default.
     * <p>
     * Should only be used on freshly {@link #clone}ed {@link ExampleSet}s to ensure that the
     * cleaned up resources are not requested afterwards.
     *
     * @since 7.3
     */
    public default void cleanup() {
        // does nothing by default
    }


    /**
     * Returns the data structure holding all attributes. NOTE! if you intend to iterate over all
     * Attributes of this ExampleSet then you need to create an Iterator by calling
     * {@link ExampleSet#getAttributes()#getAttributes()} and use it instead.
     */
    Attributes getAttributes();

    /**
     * Returns the number of examples in this example set. This number should not be used to create
     * for-loops to iterate through all examples.
     */
    int size();

    /**
     * Returns the underlying example table. Most operators should operate on the example set and
     * manipulate example to change table data instead of using the table directly.
     */
    @JsonProperty
    ExampleTable getExampleTable();

    /**
     * Returns the i-th example. It is not guaranteed that asking for an example by using the index
     * in the example table is efficiently implemented. Therefore for-loops for iterations are not
     * an option and an {@link ExampleReader} should be used.
     */
    public Example getExample(int index);

    // ------------------- Statistics ---------------

    /**
     * Recalculate all attribute statistics.
     */
    public void recalculateAllAttributeStatistics();

    /**
     * Recalculate the attribute statistics of the given attribute.
     */
    public void recalculateAttributeStatistics(Attribute attribute);

    /**
     * Returns the desired statistic for the given attribute. This method should be preferred over
     * the deprecated method Attribute#getStatistics(String) since it correctly calculates and keep
     * the statistics for the current example set and does not overwrite the statistics in the
     * attribute.
     */
    public double getStatistics(Attribute attribute, String statisticsName);

    /**
     * Returns the desired statistic for the given attribute. This method should be preferred over
     * the deprecated method Attribute#getStatistics(String) since it correctly calculates and keep
     * the statistics for the current example set and does not overwrite the statistics in the
     * attribute.
     */
    public double getStatistics(Attribute attribute, String statisticsName, String statisticsParameter);


}
