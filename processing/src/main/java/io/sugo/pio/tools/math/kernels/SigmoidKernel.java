package io.sugo.pio.tools.math.kernels;

import io.sugo.pio.tools.Tools;


/**
 * Returns the value of a Sigmoid kernel of both examples.
 * 
 */
public class SigmoidKernel extends Kernel {

	private static final long serialVersionUID = -4503893127271607088L;

	/** The parameter a of the sigmoid kernel. */
	private double a = 1.0d;

	/** The parameter b of the sigmoid kernel. */
	private double b = 0.0d;

	@Override
	public int getType() {
		return KERNEL_SIGMOID;
	}

	/** Sets the parameters of this Sigmoid kernel to the given values a and b. */
	public void setSigmoidParameters(double a, double b) {
		this.a = a;
		this.b = b;
	}

	/** Subclasses must implement this method. */
	@Override
	public double calculateDistance(double[] x1, double[] x2) {
		// K = tanh(a(x*y)+b)
		double prod = a * innerProduct(x1, x2) + b;
		double e1 = Math.exp(prod);
		double e2 = Math.exp(-prod);
		return ((e1 - e2) / (e1 + e2));
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

		String e1 = "exp(" + result.toString() + ")";
		String e2 = "exp(-1 * (" + result.toString() + "))";

		return "((" + e1 + " - " + e2 + ") / (" + e1 + " + " + e2 + "))";
	}

	@Override
	public String toString() {
		return "Sigmoid Kernel with" + Tools.getLineSeparator() + "  a: " + Tools.formatNumber(a) + Tools.getLineSeparator()
				+ "  b: " + Tools.formatNumber(b);
	}
}
