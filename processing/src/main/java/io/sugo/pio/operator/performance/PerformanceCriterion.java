package io.sugo.pio.operator.performance;


import io.sugo.pio.tools.math.Averagable;

/**
 * <p>
 * Each <tt>PerformanceCriterion</tt> contains a method to compute this criterion on a given set of
 * examples, each which has to have a real and a predicted label.
 * </p>
 * 
 * <p>
 * PerformanceCriteria must implement the <tt>compareTo</tt> method in a way that allows
 * <tt>Collections</tt> to sort the criteria in ascending order and determine the best as the
 * maximum.
 * </p>
 * 
 */
public abstract class PerformanceCriterion extends Averagable implements Comparable<PerformanceCriterion> {

	private static final long serialVersionUID = -6805251141256540352L;

	public PerformanceCriterion() {}

	/** Clone constructor. */
	public PerformanceCriterion(PerformanceCriterion o) {
		super(o);
	}

	/**
	 */
	public abstract String getDescription();

	/**
	 * Returns the number of data points which was used to determine the criterion value. If the
	 * criterion does not use example weights (or no weight was given) then the returned value will
	 * be an integer. Otherwise, the returned value is the sum of all example weights.
	 */
	public abstract double getExampleCount();

	/**
	 * <p>
	 * Returns the fitness depending on the value. The fitness values will be used for all
	 * optimization purposes (feature space transformations, parameter optimizations...) and must
	 * always be maximized. Hence, if your criterion is better the smaller the value is you should
	 * return something like (-1 * value) or (1 / value).
	 * </p>
	 * 
	 * <p>
	 * Subclasses should use {@link #getAverage()} instead of {@link #getMikroAverage()} in this
	 * method since usually the makro average (if available) should be optmized instead of the mikro
	 * average. The mikro average should only be used in the (rare) cases where no makro average is
	 * available but this is automatically done returned by {@link #getAverage()} in these cases.
	 * </p>
	 */
	public abstract double getFitness();

	/**
	 * Returns the maximum fitness. The default implementation resturns POSITIVE_INFINITY,
	 * subclasses may override this to allow feature operators to end the optimization if the
	 * maximum was reached.
	 */
	public double getMaxFitness() {
		return Double.POSITIVE_INFINITY;
	}

	/**
	 * The semantics of this method follow the specification in the interface
	 * <tt>java.lang.Comparable</tt> in the following way: Two objects of this class are equal if
	 * their <b>getFitness()</b> values are equal. The return value is 0 in this case. If the
	 * specified object is not an object of this class, a ClassCastException is thrown. If the given
	 * object has fitness bigger than this object, the return value is -1. If the given object has
	 * fitness smaller than this object, 1 is returned. No characteristics beside the fitness are
	 * used to compare two objects of this class.
	 * 
	 * @param o
	 *            Object of this class to compare this object to.
	 * @return -1, 0 or 1 if the given object is greater than, equal to, or less than this object.
	 */
	@Override
	public int compareTo(PerformanceCriterion o) {
		if (!this.getClass().equals(o.getClass())) {
			throw new RuntimeException("Mismatched criterion class:" + this.getClass() + ", " + o.getClass());
		}
		if (!o.getName().equals(this.getName())) {
			throw new RuntimeException("Mismatched criterion type:" + this.getName() + ", " + o.getName());
		}
		return Double.compare(this.getFitness(), o.getFitness());
	}
}
