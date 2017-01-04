package io.sugo.pio.ports.metadata;

/**
 * @author Simon Fischer
 */
public class MDReal extends MDNumber<Double> {

	private static final long serialVersionUID = 1L;

	public MDReal() {
		super();
	}

	public MDReal(Double number) {
		super(number);
	}

	public MDReal(MDReal real) {
		super(real);
	}

	@Override
	public MDReal add(Double add) {
		setNumber(getNumber() + add);
		return this;
	}

	@Override
	public MDReal multiply(double factor) {
		Double current = getNumber();
		if (current != null) {
			setNumber(current * factor);
		}
		return this;
	}

	@Override
	public MDReal copy() {
		return new MDReal(this);
	}

}
