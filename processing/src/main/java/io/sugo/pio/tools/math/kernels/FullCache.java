package io.sugo.pio.tools.math.kernels;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

import java.util.Iterator;


/**
 * Stores all distances in a matrix (attention: should only be used for smaller data sets).
 * 
 */
public class FullCache implements KernelCache {

	private double[][] distances;

	public FullCache(ExampleSet exampleSet, Kernel kernel) {
		int size = exampleSet.size();
		this.distances = new double[size][size];
		Iterator<Example> reader = exampleSet.iterator();
		int i = 0;
		while (reader.hasNext()) {
			Example example1 = reader.next();
			double[] x1 = new double[exampleSet.getAttributes().size()];
			int x = 0;
			for (Attribute attribute : exampleSet.getAttributes()) {
				x1[x++] = example1.getValue(attribute);
			}
			Iterator<Example> innerReader = exampleSet.iterator();
			int j = 0;
			while (innerReader.hasNext()) {
				Example example2 = innerReader.next();
				double[] x2 = new double[exampleSet.getAttributes().size()];
				x = 0;
				for (Attribute attribute : exampleSet.getAttributes()) {
					x2[x++] = example2.getValue(attribute);
				}
				double distance = kernel.calculateDistance(x1, x2);
				this.distances[i][j] = distance;
				this.distances[j][i] = distance;
				j++;
			}
			i++;
		}
	}

	@Override
	public double get(int i, int j) {
		return this.distances[i][j];
	}

	@Override
	public void store(int i, int j, double value) {
		this.distances[i][j] = value;
	}
}
