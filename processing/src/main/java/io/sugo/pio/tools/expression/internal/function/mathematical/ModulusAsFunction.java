package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.basic.Modulus;

/**
 * A {@link Function} mod() that inherits from the function for %.
 *
 * @author Gisa Schaefer
 *
 */
public class ModulusAsFunction extends Modulus {

	/**
	 * Constructs a mod-function.
	 */
	public ModulusAsFunction() {
		super("mathematical.mod", 2, Ontology.NUMERICAL);
	}

}
