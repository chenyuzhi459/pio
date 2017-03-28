package io.sugo.pio.example.table;

import java.io.Serializable;
import java.util.List;

/**
 */
public interface NominalMapping extends Cloneable, Serializable {

    /**
     * This should return true if all the mappings contain the same values regardless of their
     * internal order.
     */
    public boolean equals(NominalMapping mapping);

    /** Should return a deep clone of this nominal mapping. */
    public Object clone();

    /**
     * Returns the index of a positive class (if available). Returns -1 otherwise.
     */
    public int getPositiveIndex();

    /**
     * Returns the nominal value of a positive class (if available). Returns null otherwise.
     */
    public String getPositiveString();

    /**
     * Returns the index of a negative class (if available). Returns -1 otherwise.
     */
    public int getNegativeIndex();

    /**
     * Returns the nominal value of a negative class (if available). Returns null otherwise.
     */
    public String getNegativeString();


    /**
     * Returns the internal double representation (actually an integer index) for the given nominal
     * value without creating a mapping if none exists.
     *
     * @return the integer of the index or -1 if no mapping for this value exists
     */
    public int getIndex(String nominalValue);

    /**
     * Returns the nominal value for an internal double representation (actually an integer index).
     * This method only works for nominal values which were formerly mapped via
     * {@link #mapString(String)}.
     */
    public String mapIndex(int index);

    /**
     * Sets the given mapping. This might be practical for example for replacing a nominal value
     * (without a data scan!).
     */
    public void setMapping(String nominalValue, int index);

    /**
     * Returns the internal double representation (actually an integer index) for the given nominal
     * value. This method creates a mapping if it did not exist before.
     */
    public int mapString(String nominalValue);

    /**
     * Returns a list of all nominal values which were mapped via {@link #mapString(String)} until
     * now.
     */
    public List<String> getValues();

    /**
     * Returns the number of different nominal values which were mapped via
     * {@link #mapString(String)} until now.
     */
    public int size();

    /**
     * This method rearranges the string to number mappings such that they are in alphabetical
     * order. <br>
     * <b>VERY IMPORTANT NOTE:</b> Do not call this method when this attribute is already associated
     * with an {@link AbstractExampleTable} and it already contains {@link Example}s. All examples
     * will be messed up otherwise!
     */
    public void sortMappings();

    /** Clears the mapping. */
    public void clear();
}
