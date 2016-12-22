package io.sugo.pio.example.table;

import java.io.Serializable;
import java.util.List;

/**
 */
public interface NominalMapping extends Cloneable, Serializable {
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
}
