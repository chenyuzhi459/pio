package io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel;

/**
 * Gaussian Combination Kernel
 * 
 */
public class KernelMultiquadric extends Kernel {

	private static final long serialVersionUID = -9152135200919885773L;

	private double sigma = 1;
	private double shift = 1;

	/** Output as String */
	@Override
	public String toString() {
		return ("multiquadric(sigma=" + sigma + ",shift=" + shift + ")");
	};

	/** Class constructor. */
	public KernelMultiquadric() {}

	public void setParameters(double sigma, double shift) {
		this.sigma = sigma;
		this.shift = shift;
	}

	/** Calculates kernel value of vectors x and y. */
	@Override
	public double calculate_K(int[] x_index, double[] x_att, int[] y_index, double[] y_att) {
		return Math.sqrt((norm2(x_index, x_att, y_index, y_att) / sigma) + (shift * shift));
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
}
