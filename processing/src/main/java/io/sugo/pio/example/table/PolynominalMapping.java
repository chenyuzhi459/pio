package io.sugo.pio.example.table;

import io.sugo.pio.tools.Tools;

import java.util.*;


/**
 * This is an implementation of {@link NominalMapping} which can be used for nominal attributes with
 * an arbitrary number of different values.
 *
 * @author Ingo Mierswa
 */
public class PolynominalMapping implements NominalMapping {

	private static final long serialVersionUID = 5021638750496191771L;

	/** The map between symbolic values and their indices. */
	private final Map<String, Integer> symbolToIndexMap = new LinkedHashMap<>();

	/** The map between indices of nominal values and the actual nominal value. */
	private final List<String> indexToSymbolMap = new ArrayList<>();

	public PolynominalMapping() {}

	public PolynominalMapping(Map<Integer, String> map) {
		this.symbolToIndexMap.clear();
		this.indexToSymbolMap.clear();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			int index = entry.getKey();
			String value = entry.getValue();
			this.symbolToIndexMap.put(value, index);
			while (this.indexToSymbolMap.size() <= index) {
				this.indexToSymbolMap.add(null);
			}
			this.indexToSymbolMap.set(index, value);
		}
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
		// lookup string in hashtable
		int index = getIndex(str);
		// if string is not yet in the map, add it
		if (index < 0) {
			indexToSymbolMap.add(str);
			index = indexToSymbolMap.size() - 1;
			symbolToIndexMap.put(str, index);
		}
		return index;
	}

	/**
	 * Returns the index of the given nominal value or -1 if this value was not mapped before by
	 * invoking the method .
	 */
	@Override
	public int getIndex(String str) {
		Integer index = symbolToIndexMap.get(str);
		if (index == null) {
			return -1;
		} else {
			return index.intValue();
		}
	}


	/** Returns the values of the attribute as an enumeration of strings. */
	@Override
	public List<String> getValues() {
		return indexToSymbolMap;
	}

	@Override
	public String toString() {
		return indexToSymbolMap.toString() + Tools.getLineSeparator() + symbolToIndexMap.toString();
	}
}
