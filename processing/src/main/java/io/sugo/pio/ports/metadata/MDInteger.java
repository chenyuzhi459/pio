package io.sugo.pio.ports.metadata;

/**
 * @author Simon Fischer
 */
public class MDInteger extends MDNumber<Integer> {

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor will build a unkown number
	 */
	public MDInteger() {
		super(0);
	}

	public MDInteger(int i) {
		super(i);
	}

	public MDInteger(MDInteger integer) {
		super(integer);
	}

	@Override
	public MDInteger add(Integer add) {
		setNumber(getNumber() + add);
		return this;
	}

	public void add(MDInteger add) {
		setNumber(getNumber() + add.getNumber());
		switch (add.getRelation()) {
			case AT_LEAST:
				increaseByUnknownAmount();
				break;
			case AT_MOST:
				reduceByUnknownAmount();
				break;
			case UNKNOWN:
				setUnkown();
		}
	}

	@Override
	public MDInteger multiply(double factor) {
		Integer current = getNumber();
		if (current != null) {
			setNumber((int) Math.round(current * factor));
		}
		return this;
	}

	public MDInteger subtract(int subtrahend) {
		Integer current = getNumber();
		if (current != null) {
			setNumber(current - subtrahend);
		}
		return this;
	}

	@Override
	public MDInteger copy() {
		return new MDInteger(this);
	}

	@Override
	public String toString() {
		switch (getRelation()) {
			case EQUAL:
				return "= " + getValue();
			case AT_LEAST:
				return "\u2265 " + getNumber();
			case AT_MOST:
				return "\u2264 " + getNumber();
			case UNKNOWN:
			default:
				return "?";
		}
	}

}
