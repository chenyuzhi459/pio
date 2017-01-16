package io.sugo.pio.example.table;

import java.util.List;

/**
 */
public class BinominalMapping implements NominalMapping {
    /** The index of the first value. */
    private static final int FIRST_VALUE_INDEX = 0;

    /** The index of the second value. */
    private static final int SECOND_VALUE_INDEX = 1;

    /**
     * Nominal index of the value that will be treated as the "positive" value of this attribute.
     */
    public static final int POSITIVE_INDEX = SECOND_VALUE_INDEX;

    /**
     * Nominal index of the value that will be treated as the "negative" value of this attribute.
     */
    public static final int NEGATIVE_INDEX = FIRST_VALUE_INDEX;

    /** The first nominal value. */
    private String firstValue = null;

    /** The second nominal value. */
    private String secondValue = null;

    public BinominalMapping() {}

    /** Clone constructor. */
    private BinominalMapping(BinominalMapping mapping) {
        this.firstValue = mapping.firstValue;
        this.secondValue = mapping.secondValue;
    }

    @Override
    public Object clone() {
        return new BinominalMapping(this);
    }

    @Override
    public int mapString(String str) {
        if (str == null) {
            return -1;
        }
        // lookup string
        int index = getIndex(str);
        if (index < 0) {
            // if string is not found, set it
            if (firstValue == null) {
                firstValue = str;
                return FIRST_VALUE_INDEX;
            } else if (secondValue == null) {
                secondValue = str;
                return SECOND_VALUE_INDEX;
            } else {
                throw new RuntimeException(
                        "Cannot map another string for binary attribute: already mapped two strings (" + firstValue + ", "
                                + secondValue + "). The third string that was tried to add: '" + str + "'");
            }
        } else {
            return index;
        }
    }

    /**
     * Returns the index of the given nominal value or -1 if this value was not mapped before by
     * invoking the method {@link #mapIndex(int)}.
     */
    @Override
    public int getIndex(String str) {
        if (str.equals(firstValue)) {
            return FIRST_VALUE_INDEX;
        } else if (str.equals(secondValue)) {
            return SECOND_VALUE_INDEX;
        } else {
            return -1;
        }
    }

    @Override
    public String mapIndex(int index) {
        return null;
    }

    /**
     * Returns the index of the first value if this attribute is a classification attribute, i.e. if
     * it is binominal.
     */
    @Override
    public int getNegativeIndex() {
        return NEGATIVE_INDEX;
    }

    /**
     * Returns the index of the second value if this attribute is a classification attribute. Works
     * for all binominal attributes.
     */
    @Override
    public int getPositiveIndex() {
        return POSITIVE_INDEX;
    }

    @Override
    public String getNegativeString() {
        return firstValue;
    }

    @Override
    public String getPositiveString() {
        return secondValue;
    }


    @Override
    public List<String> getValues() {
        return null;
    }

    /** Returns the number of different nominal values. */
    @Override
    public int size() {
        if (firstValue == null) {
            return 0;
        } else if (secondValue == null) {
            return 1;
        } else {
            return 2;
        }
    }
}
