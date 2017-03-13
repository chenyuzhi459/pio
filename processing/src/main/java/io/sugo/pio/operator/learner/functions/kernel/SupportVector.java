package io.sugo.pio.operator.learner.functions.kernel;

import java.io.Serializable;


/**
 * Holds all information of a support vector, i.e. the attribute values, the label, and the alpha.
 * 
 */
public class SupportVector implements Serializable {

	private static final long serialVersionUID = -8544548121343344760L;

	private double[] x;

	private double y;

	private double alpha;

	/** Creates a new support vector. */
	public SupportVector(double[] x, double y, double alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}

	public double[] getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getAlpha() {
		return alpha;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < x.length; i++) {
			result.append(x[i] + " ");
		}
		result.append("alpha=" + alpha);
		result.append(" y=" + y);
		return result.toString();
	}
}
