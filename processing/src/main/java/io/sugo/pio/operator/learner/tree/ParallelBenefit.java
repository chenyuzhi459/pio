package io.sugo.pio.operator.learner.tree;

/**
 * Encapsulates some information about the benefit of a split.
 *
 *
 * @author Ingo Mierswa, Gisa Schaefer
 */
public class ParallelBenefit implements Comparable<ParallelBenefit> {

	private int attributeNumber;

	private double benefit;

	private double splitValue;

	public ParallelBenefit(double benefit, int attributeNumber) {
		this(benefit, attributeNumber, Double.NaN);
	}

	public ParallelBenefit(double benefit, int attributeNumber, double splitValue) {
		this.benefit = benefit;
		this.attributeNumber = attributeNumber;
		this.splitValue = splitValue;
	}

	public int getAttributeNumber() {
		return this.attributeNumber;
	}

	public double getSplitValue() {
		return this.splitValue;
	}

	public double getBenefit() {
		return this.benefit;
	}

	@Override
	public String toString() {
		return "Attribute number= " + attributeNumber + ", benefit = " + benefit
				+ (!Double.isNaN(splitValue) ? ", split = " + splitValue : "");
	}

	@Override
	public int compareTo(ParallelBenefit o) {
		// needed to guarantee that each calculation gives the same result even if they are done in
		// parallel
		if (Double.compare(this.benefit, o.benefit) == 0) {
			return Integer.compare(this.attributeNumber, o.attributeNumber);
		}
		return -1 * Double.compare(this.benefit, o.benefit);
	}
}
