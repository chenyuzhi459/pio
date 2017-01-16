package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;

/**
 * Encapsulates some information about the benefit of a split.
 * 
 * 
 * @author Ingo Mierswa
 */
public class Benefit implements Comparable<Benefit> {

	private Attribute attribute;

	private double benefit;

	private double splitValue;

	public Benefit(double benefit, Attribute attribute) {
		this(benefit, attribute, Double.NaN);
	}

	public Benefit(double benefit, Attribute attribute, double splitValue) {
		this.benefit = benefit;
		this.attribute = attribute;
		this.splitValue = splitValue;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public double getSplitValue() {
		return this.splitValue;
	}

	public double getBenefit() {
		return this.benefit;
	}

	@Override
	public String toString() {
		return "Attribute = " + attribute.getName() + ", benefit = " + benefit
				+ (!Double.isNaN(splitValue) ? ", split = " + splitValue : "");
	}

	@Override
	public int compareTo(Benefit o) {
		return -1 * Double.compare(this.benefit, o.benefit);
	}
}
