package io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples;

import java.io.Serializable;


/**
 * An Example for the kernel based algorithms provided by Stefan Rueping. Since RapidMiner cannot
 * deliver the example with index i directly, a new data structure is needed.
 * 
 */
public class SVMExample implements Serializable {

	private static final long serialVersionUID = 8539279195547132597L;

	public int[] index;

	public double[] att;

	public SVMExample() {
		index = null;
		att = null;
	}

	public SVMExample(double[] values) {
		index = new int[values.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = i;
		}
		this.att = values;
	}

	/*
	 * For internal purposes only!!!
	 */
	public SVMExample(SVMExample e) {
		this.index = e.index;
		this.att = e.att;
	}

	public SVMExample(int[] new_index, double[] new_att) {
		index = new_index;
		att = new_att;
	}

	public double[] toDense(int dim) {
		double[] dense;
		dense = new double[dim];
		int pos = 0;
		if (index != null) {
			for (int i = 0; i < index.length; i++) {
				while (pos < index[i]) {
					dense[pos] = 0.0;
					pos++;
				}
				dense[pos] = att[i];
				pos++;
			}
		}
		while (pos < dim) {
			dense[pos] = 0.0;
			pos++;
		}
		return dense;
	}
}
