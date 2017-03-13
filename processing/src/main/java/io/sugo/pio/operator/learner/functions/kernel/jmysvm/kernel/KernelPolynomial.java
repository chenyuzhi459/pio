package io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel;

import io.sugo.pio.tools.Tools;


/**
 * Polynomial Kernel
 * 
 */
public class KernelPolynomial extends Kernel {

	private static final long serialVersionUID = 7385441798122306059L;

	private double degree = 2;

	/**
	 * Class constructor
	 */
	public KernelPolynomial() {};

	/**
	 * Output as String
	 */
	@Override
	public String toString() {
		return ("poly(" + degree + ")");
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	/**
	 * Calculates kernel value of vectors x and y
	 */
	@Override
	public double calculate_K(int[] x_index, double[] x_att, int[] y_index, double[] y_att) {
		double prod = innerproduct(x_index, x_att, y_index, y_att);
		double result = prod;
		for (int i = 1; i < degree; i++) {
			result *= prod;
		}
		return result;
	}

	@Override
	public String getDistanceFormula(double[] x, String[] attributeConstructions) {
		StringBuffer innerProductString = new StringBuffer();
		boolean first = true;
		for (int i = 0; i < x.length; i++) {
			double value = x[i];
			if (!Tools.isZero(value)) {
				if (value < 0.0d) {
					if (first) {
						innerProductString.append("-" + Math.abs(value) + " * " + attributeConstructions[i]);
					} else {
						innerProductString.append(" - " + Math.abs(value) + " * " + attributeConstructions[i]);
					}
				} else {
					if (first) {
						innerProductString.append(value + " * " + attributeConstructions[i]);
					} else {
						innerProductString.append(" + " + value + " * " + attributeConstructions[i]);
					}
				}
				first = false;
			}
		}

		StringBuffer result = new StringBuffer("(" + innerProductString.toString() + ")");
		for (int i = 1; i < degree; i++) {
			result.append(" * (" + innerProductString.toString() + ")");
		}
		return result.toString();
	}

	public double getDegree() {
		return degree;
	}
}
