package io.sugo.pio.operator.learner.associations;

import java.io.Serializable;


/**
 * Item the base class for itemsets and provide all necessary frequency information.
 */
public interface Item extends Comparable<Item>, Serializable {

    /**
     * This method returns the frequency of this item
     *
     * @return the frequency of this item
     */
    public int getFrequency();

    /**
     * This method adds one to the frequency of this item
     */
    public void increaseFrequency();

    /**
     * This method increases the frequency of this item by value
     *
     * @param value is added to the frequency
     */
    public void increaseFrequency(int value);

    public String getName();

    /**
     * This method returns a human readable String representation of this item.
     *
     * @return the representing string
     */
    @Override
    public String toString();

}
