package io.sugo.pio.example;

import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.Annotations;

import java.io.Serializable;
import java.util.Iterator;

/**
 */
public interface Attribute extends Cloneable, Serializable {

    /** Used to identify that this attribute is not part of any example table. */
    public static final int UNDEFINED_ATTRIBUTE_INDEX = -1;

    /** Used to identify view attributes */
    public static final int VIEW_ATTRIBUTE_INDEX = -2;

    /**
     * Indicates a missing value for nominal values. For the internal values and numerical values,
     * Double.NaN is used which can be checked via {@link Double#isNaN(double)}.
     */
    public static final String MISSING_NOMINAL_VALUE = "?";

    /**
     * Clones this attribute.
     */
    Object clone();

    /**
     * Returns the name of the attribute.
     */
    String getName();

    /**
     * Sets the name of the attribute.
     */
    void setName(String name);

    /**
     * Returns the index in the example table.
     */
    int getTableIndex();

    /**
     * Sets the index in the example table.
     */
    void setTableIndex(int index);

    /**
     * Sets the Attributes instance to which this attribute belongs. This instance will be notified
     * when the attribute renames itself. This method must not be called except by the
     * {@link Attributes} to which this AttributeRole is added.
     */
    void addOwner(Attributes attributes);

    void removeOwner(Attributes attributes);

    /**
     * Returns the value for the column this attribute corresponds to in the given data row.
     */
    double getValue(DataRow row);

    /**
     * Sets the value for the column this attribute corresponds to in the given data row.
     */
    void setValue(DataRow row, double value);

    public void addTransformation(AttributeTransformation transformation);

    public AttributeTransformation getLastTransformation();

    /** Clear all transformations. */
    public void clearTransformations();

    /**
     * Returns an iterator over all statistics objects available for this type of attribute.
     * Additional statistics can be registered via {@link #registerStatistics(Statistics)}.
     */
    public Iterator<Statistics> getAllStatistics();

    /** Returns the construction description. */
    public String getConstruction();

    /** Registers the attribute statistics. */
    public void registerStatistics(Statistics statistics);

    /**
     * Sets the construction description.
     */
    void setConstruction(String description);

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an
     * {@link UnsupportedOperationException} for non-nominal attributes.
     */
    NominalMapping getMapping();

    /**
     * Returns the nominal mapping between nominal values and internal double representations.
     * Please note that invoking this method might result in an exception for non-nominal
     * attributes.
     */
    void setMapping(NominalMapping nominalMapping);

    /**
     * Returns the block type of this attribute.
     *
     * @see io.sugo.pio.tools.Ontology#ATTRIBUTE_BLOCK_TYPE
     */
    public int getBlockType();

    /**
     * Sets the block type of this attribute.
     *
     * @see io.sugo.pio.tools.Ontology#ATTRIBUTE_BLOCK_TYPE
     */
    public void setBlockType(int b);

    /** Sets the default value for this attribute. */
    public void setDefault(double value);

    /** Returns the default value for this attribute. */
    public double getDefault();

    /**
     * Returns true if the attribute is nominal.
     */
    boolean isNominal();

    /**
     * Returns true if the attribute is numerical.
     */
    boolean isNumerical();

    /**
     * Returns true if the attribute is date_time.
     */
    boolean isDateTime();

    /**
     * Returns a formatted string of the given value according to the attribute type.
     */
    String getAsString(double value, int digits, boolean quoteNominal);

    /**
     * Returns the value type of this attribute.
     *
     * @see io.sugo.pio.tools.Ontology#ATTRIBUTE_VALUE_TYPE
     */
    int getValueType();

    /** Returns a set of annotations for this attribute. */
    public Annotations getAnnotations();
}
