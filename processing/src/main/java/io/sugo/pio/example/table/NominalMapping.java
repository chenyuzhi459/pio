package io.sugo.pio.example.table;

import java.io.Serializable;
import java.util.List;

/**
 */
public interface NominalMapping extends Cloneable, Serializable {
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
}
