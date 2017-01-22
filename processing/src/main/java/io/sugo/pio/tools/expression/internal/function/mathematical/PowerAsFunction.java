package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.basic.Power;

/**
 * A {@link Function} for pow() that inherits from the function for ^.
 *
 * @author Gisa Schaefer
 *
 */
public class PowerAsFunction extends Power {

	/**
	 * Constructs a pow-function.
	 */
	public PowerAsFunction() {
		super("mathematical.pow", 2, Ontology.NUMERICAL);
	}

}
