package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.table.DataRow;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * This is an {@link Aggregator} for the {@link ModeAggregationFunction}. If the mode is not unique,
 * the first value from the nominal mapping will be used.
 * 
 */
public class ModeAggregator implements Aggregator {

	private Attribute sourceAttribute;
	private double[] frequencies;
	private HashMap<Double, Double> frequenciesMap;

	public ModeAggregator(AggregationFunction function) {
		this.sourceAttribute = function.getSourceAttribute();
		if (sourceAttribute.isNominal()) {
			frequencies = new double[sourceAttribute.getMapping().size()];
		} else {
			frequenciesMap = new HashMap<Double, Double>();
		}
	}

	@Override
	public void count(Example example) {
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			if (frequencies != null) {
				frequencies[(int) value]++;
			} else {
				Double frequency = frequenciesMap.get(value);
				if (frequency == null) {
					frequenciesMap.put(value, 1d);
				} else {
					frequenciesMap.put(value, frequency + 1d);
				}
			}
		}
	}

	@Override
	public void count(Example example, double weight) {
		double value = example.getValue(sourceAttribute);
		if (!Double.isNaN(value)) {
			if (frequencies != null) {
				frequencies[(int) value] += weight;
			} else {
				Double frequency = frequenciesMap.get(value);
				if (frequency == null) {
					frequenciesMap.put(value, weight);
				} else {
					frequenciesMap.put(value, frequency + weight);
				}
			}
		}
	}

	@Override
	public void set(Attribute attribute, DataRow row) {
		double minValue = -1;
		double minFrequency = Double.NEGATIVE_INFINITY;

		if (frequencies != null) {
			for (int i = 0; i < frequencies.length; i++) {
				if (frequencies[i] > minFrequency) {
					minValue = i;
					minFrequency = frequencies[i];
				}
			}
		} else {
			for (Entry<Double, Double> entry : frequenciesMap.entrySet()) {
				double frequency = entry.getValue();
				if (frequency > minFrequency) {
					minValue = entry.getKey();
					minFrequency = frequency;
				}

			}
		}
		// if any counter was greater 0, set result to maximum
		if (minValue > -1) {
			row.set(attribute, minValue);
		} else {
			row.set(attribute, Double.NaN);
		}
	}
}
