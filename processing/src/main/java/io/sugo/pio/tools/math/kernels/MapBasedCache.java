package io.sugo.pio.tools.math.kernels;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores all distances in a map. Uses only a fixed maximum amount of entries for this map (default:
 * 10,000,000, enough for about 3000 examples).
 * 
 */
public class MapBasedCache implements KernelCache {

	private int maxSize = 10000000;

	private int exampleSetSize;

	private int accessCounter = 0;

	private Map<Integer, Integer> accessMap;

	private Map<Integer, Double> entries;

	public MapBasedCache(int exampleSetSize) {
		this(10000000, exampleSetSize);
	}

	public MapBasedCache(int maxSize, int exampleSetSize) {
		this.maxSize = maxSize;
		this.exampleSetSize = exampleSetSize;
		this.accessMap = new HashMap<Integer, Integer>(maxSize);
		this.entries = new HashMap<Integer, Double>(maxSize);
	}

	@Override
	public double get(int i, int j) {
		accessCounter++;
		Double result = entries.get(i * exampleSetSize + j);
		if (result == null) {
			return Double.NaN;
		} else {
			accessMap.put(i * exampleSetSize + j, accessCounter);
			return result;
		}
	}

	@Override
	public void store(int i, int j, double value) {
		if (accessMap.size() > this.maxSize) {
			int oldestKey = -1;
			int oldestAcess = Integer.MAX_VALUE;
			Iterator<Map.Entry<Integer, Integer>> it = accessMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Integer> entry = it.next();
				int access = entry.getValue();
				if (access < oldestAcess) {
					oldestKey = entry.getKey();
					oldestAcess = access;
				}
			}

			if (oldestKey != -1) {
				accessMap.remove(oldestKey);
				entries.remove(oldestKey);
			}
		}

		accessMap.put(i * exampleSetSize + j, accessCounter);
		entries.put(i * exampleSetSize + j, value);
	}
}
