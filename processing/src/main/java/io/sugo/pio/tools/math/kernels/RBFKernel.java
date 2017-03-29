package io.sugo.pio.tools.math.kernels;

import io.sugo.pio.tools.Tools;


/**
 * Returns the value of the RBF kernel of both examples.
 * 
 */
public class RBFKernel extends Kernel {

	private static final long serialVersionUID = 2928962529445448574L;

	/** The parameter gamma of the RBF kernel. */
	private double gamma = -1.0d;

	@Override
	public int getType() {
		return KERNEL_RADIAL;
	}

	public void setGamma(double gamma) {
		this.gamma = -gamma;
	}

	public double getGamma() {
		return -gamma;
	}

	/** Calculates kernel value of vectors x and y. */
	@Override
	public double calculateDistance(double[] x1, double[] x2) {
		return (Math.exp(this.gamma * norm2(x1, x2))); // gamma = -params.gamma
	}

	@Override
	public String getDistanceFormula(double[] x, String[] attributeConstructions) {
		StringBuffer result = new StringBuffer("exp(");
		result.append(gamma + " * (");

		boolean first = true;
		for (int i = 0; i < x.length; i++) {
			double value = x[i];
			String valueString = "(" + value + " - " + attributeConstructions[i] + ")";
			if (first) {
				result.append(valueString + " * " + valueString);
			} else {
				result.append(" + " + valueString + " * " + valueString);
			}
			first = false;
		}
		result.append("))");
		return result.toString();
	}

	@Override
	public String toString() {
		return "RBF Kernel with" + Tools.getLineSeparator() + "  gamma: " + Tools.formatNumber(getGamma());
	}
}
