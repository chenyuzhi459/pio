package io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel;

import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples;
import io.sugo.pio.tools.Tools;


/**
 * Linear Kernel
 * 
 */
public class KernelDot extends Kernel {

	private static final long serialVersionUID = -6384697098131949237L;

	/** Class constructor */
	public KernelDot() {}

	/** Output as String */
	@Override
	public String toString() {
		return ("linear");
	}

	/**
	 * Class constructor
	 * 
	 * @param examples
	 *            Container for the examples.
	 */
	public KernelDot(SVMExamples examples, int cacheSize) {
		init(examples, cacheSize);
	}

	/**
	 * Calculates kernel value of vectors x and y
	 */
	@Override
	public double calculate_K(int[] x_index, double[] x_att, int[] y_index, double[] y_att) {
		return innerproduct(x_index, x_att, y_index, y_att);
	}

	@Override
	public String getDistanceFormula(double[] x, String[] attributeConstructions) {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < x.length; i++) {
			double value = x[i];
			if (!Tools.isZero(value)) {
				if (value < 0.0d) {
					if (first) {
						result.append("-" + Math.abs(value) + " * " + attributeConstructions[i]);
					} else {
						result.append(" - " + Math.abs(value) + " * " + attributeConstructions[i]);
					}
				} else {
					if (first) {
						result.append(value + " * " + attributeConstructions[i]);
					} else {
						result.append(" + " + value + " * " + attributeConstructions[i]);
					}
				}
				first = false;
			}
		}
		return result.toString();
	}
};
