package io.sugo.pio.example;


/**
 * The superclass for all attribute statistics objects.
 * 
 * @author Ingo Mierswa
 */
public class UnknownStatistics implements Statistics {

	private static final long serialVersionUID = 217609774484151520L;

	private int unknownCounter = 0;

	public UnknownStatistics() {}

	/** Clone constructor. */
	private UnknownStatistics(UnknownStatistics other) {
		this.unknownCounter = other.unknownCounter;
	}

	@Override
	public Object clone() {
		return new UnknownStatistics(this);
	}

	@Override
	public void startCounting(Attribute attribute) {
		this.unknownCounter = 0;
	}

	@Override
	public void count(double value, double weight) {
		if (Double.isNaN(value)) {
			unknownCounter++;
		}
	}

	@Override
	public double getStatistics(Attribute attribute, String statisticsName, String parameter) {
		if (UNKNOWN.equals(statisticsName)) {
			return unknownCounter;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public boolean handleStatistics(String statisticsName) {
		return UNKNOWN.equals(statisticsName);
	}

	@Override
	public String toString() {
		return "unknown: " + this.unknownCounter;
	}
}
