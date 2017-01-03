package io.sugo.pio.example.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PolynominalMapping implements NominalMapping {
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
     * invoking the method {@link #mapIndex(int)}.
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

    /**
     * Returns the attribute value, that is associated with this index. Index counting starts with
     * 0. <b>WARNING:</b> In order to iterate over all values please use the collection returned by
     * {@link #getValues()}.
     */
    @Override
    public String mapIndex(int index) {
        if (index < 0 || index >= indexToSymbolMap.size()) {
            throw new RuntimeException(
                    "Cannot map index of nominal attribute to nominal value: index " + index + " is out of bounds!");
        }
        return indexToSymbolMap.get(index);
    }

    @Override
    public List<String> getValues() {
        return null;
    }
}