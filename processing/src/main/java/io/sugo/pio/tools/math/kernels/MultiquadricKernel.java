package io.sugo.pio.tools.math.kernels;

import io.sugo.pio.tools.Tools;

/**
 * Returns the value of the Multiquadric kernel of both examples.
 * 
 */
public class MultiquadricKernel extends Kernel {

	private static final long serialVersionUID = -7896178642575555770L;

	/** The parameter sigma of the Multiquadric kernel. */
	private double sigma = 1.0d;

	/** The parameter shift of the multiquadric kernel. */
	private double shift = 1.0d;

	@Override
	public int getType() {
		return KERNEL_MULTIQUADRIC;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getSigma() {
		return sigma;
	}

	public void setShift(double shift) {
		this.shift = shift;
	}

	public double getShift() {
		return shift;
	}

	/** Calculates kernel value of vectors x and y. */
	@Override
	public double calculateDistance(double[] x1, double[] x2) {
		return Math.sqrt((norm2(x1, x2) / sigma) + (shift * shift));
	}

	@Override
	public String getDistanceFormula(double[] x, String[] attributeConstructions) {

		StringBuffer norm2Expression = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < x.length; i++) {
			double value = x[i];
			String valueString = "(" + value + " - " + attributeConstructions[i] + ")";
			if (first) {
				norm2Expression.append(valueString + " * " + valueString);
			} else {
				norm2Expression.append(" + " + valueString + " * " + valueString);
			}
			first = false;
		}

		return "sqrt((" + norm2Expression + " / " + sigma + ") + (" + shift + " * " + shift + "))";
	}

	@Override
	public String toString() {
		return "Multiquadric Kernel with" + Tools.getLineSeparator() + "  sigma: " + Tools.formatNumber(getSigma())
				+ Tools.getLineSeparator() + "  shift: " + Tools.formatNumber(getShift());
	}
}
