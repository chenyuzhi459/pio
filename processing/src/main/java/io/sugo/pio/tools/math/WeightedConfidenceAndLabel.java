package io.sugo.pio.tools.math;

import java.util.Comparator;


/**
 * Helper class for finding thresholds for cost sensitive learning or calculating the AUC
 * performance criterion.
 * 
 */
public class WeightedConfidenceAndLabel implements Comparable {

	public static class WCALComparator implements Comparator<WeightedConfidenceAndLabel> {

		private ROCBias method;

		public WCALComparator(ROCBias method) {
			this.method = method;
		}

		@Override
		public int compare(WeightedConfidenceAndLabel o1, WeightedConfidenceAndLabel o2) {
			int compi = (-1) * Double.compare(o1.confidence, o2.confidence);
			if (compi == 0) {
				switch (method) {
					case OPTIMISTIC:
						return -Double.compare(o1.label, o2.label);
					case PESSIMISTIC:
					case NEUTRAL:
					default:
						return Double.compare(o1.label, o2.label);
				}
			} else {
				return compi;
			}
		}

	}

	private final double confidence, label, prediction;

	private double weight = 1.0d;

	public WeightedConfidenceAndLabel(double confidence, double label, double prediction) {
		this(confidence, label, prediction, 1.0d);
	}

	public WeightedConfidenceAndLabel(double confidence, double label, double prediction, double weight) {
		this.confidence = confidence;
		this.label = label;
		this.prediction = prediction;
		this.weight = weight;
	}

	@Override
	public int compareTo(Object obj) {
		// We need to sort the examples by *decreasing* confidence:
		int compi = (-1) * Double.compare(this.confidence, ((WeightedConfidenceAndLabel) obj).confidence);
		if (compi == 0) {
			return -Double.compare(this.label, ((WeightedConfidenceAndLabel) obj).label);
		} else {
			return compi;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof WeightedConfidenceAndLabel)) {
			return false;
		} else {
			WeightedConfidenceAndLabel l = (WeightedConfidenceAndLabel) o;
			return (this.label == l.label) && (this.confidence == l.confidence);
		}
	}

	@Override
	public int hashCode() {
		return Double.valueOf(this.label).hashCode() ^ Double.valueOf(this.confidence).hashCode();
	}

	public double getLabel() {
		return this.label;
	}

	public double getPrediction() {
		return this.prediction;
	}

	public double getConfidence() {
		return this.confidence;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "conf: " + confidence + ", label: " + label + ", weight: " + weight;
	}
}
