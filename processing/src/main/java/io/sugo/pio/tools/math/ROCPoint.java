package io.sugo.pio.tools.math;

import io.sugo.pio.tools.Tools;

import java.io.Serializable;


/**
 * Objects of this class hold all information about a single ROC data point.
 * 
 */
public class ROCPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double falsePositives;

	private final double truePositives;

	private final double confidence;

	public ROCPoint(double falsePositives, double truePositives, double confidence) {
		this.falsePositives = falsePositives;
		this.truePositives = truePositives;
		this.confidence = confidence;
	}

	/** Returns the number of false positives, not the rate. */
	public double getFalsePositives() {
		return falsePositives;
	}

	/** Returns the number of true positives, not the rate. */
	public double getTruePositives() {
		return truePositives;
	}

	public double getConfidence() {
		return confidence;
	}

	@Override
	public String toString() {
		return "fp: " + Tools.formatIntegerIfPossible(falsePositives) + ", tp: "
				+ Tools.formatIntegerIfPossible(truePositives) + ", confidence: "
				+ Tools.formatIntegerIfPossible(confidence);
	}
}
