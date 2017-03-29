package io.sugo.pio.generator;

import com.metamx.common.logger.Logger;

import java.util.logging.Level;


/**
 * Creates the reciprocal value of all input attributes. If the generator is bounded, the values are
 * bounded by the biggest and smallest possible values.
 * 
 * @author Simon Fischer, Ingo Mierswa ingomierswa Exp $
 */
public class ReciprocalValueGenerator extends SingularNumericalGenerator {

	private static final Logger log = new Logger(ReciprocalValueGenerator.class);

	public static final String FUNCTION_NAMES[] = { "1/" };

	public ReciprocalValueGenerator() {}

	@Override
	public FeatureGenerator newInstance() {
		return new ReciprocalValueGenerator();
	}

	@Override
	public double calculateValue(double value) {
		return 1.0d / value;
	}

	@Override
	public void setFunction(String name) {
		for (int i = 0; i < FUNCTION_NAMES.length; i++) {
			if (FUNCTION_NAMES[i].equals(name)) {
				return;
			}
		}
		log.error("io.sugo.pio.generator.ReciprocalValueGenerator.illegal_function_name",
				new Object[] { name, getClass().getName() });
	}

	@Override
	public String getFunction() {
		return FUNCTION_NAMES[0];
	}
}
