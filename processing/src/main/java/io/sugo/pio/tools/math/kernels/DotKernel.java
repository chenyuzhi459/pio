package io.sugo.pio.tools.math.kernels;

import io.sugo.pio.tools.Tools;


/**
 * Returns the simple inner product of both examples.
 * 
 */
public class DotKernel extends Kernel {

	private static final long serialVersionUID = -7737835520088841652L;

	@Override
	public int getType() {
		return KERNEL_DOT;
	}

	/** Subclasses must implement this method. */
	@Override
	public double calculateDistance(double[] x1, double[] x2) {
		return innerProduct(x1, x2);
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

	@Override
	public String toString() {
		return "Dot Kernel";
	}
}
