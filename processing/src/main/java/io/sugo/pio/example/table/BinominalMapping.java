package io.sugo.pio.example.table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * This is an efficient implementation of a {@link NominalMapping} which can be used for binominal
 * attributes, i.e. for attributes with only two different values.
 *
 * @author Ingo Mierswa
 */
public class BinominalMapping implements NominalMapping {

	private static final long serialVersionUID = 6566553739308153153L;

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

	/**
	 * Returns the index for the nominal attribute value <code>str</code>. If the string is unknown,
	 * a new index value is assigned. Returns -1, if str is null.
	 */
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
				throw new AttributeTypeException(
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


	/** Returns the values of the attribute as an enumeration of strings. */
	@Override
	public List<String> getValues() {
		if (firstValue == null) {
			return Collections.emptyList();
		} else if (secondValue == null) {
			return Arrays.asList(firstValue);
		} else {
			return Arrays.asList(firstValue, secondValue);
		}
	}
}
