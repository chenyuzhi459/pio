package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.OperatorProcess;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

/**
 * A {@link Function} that delivers random numbers.
 *
 * @author Gisa Schaefer
 *
 */
public class Random extends AbstractFunction {

	private final OperatorProcess process;

	/**
	 * Creates a function that delivers random numbers using the random generator associated to the
	 * process.
	 *
	 * @param process
	 */
	public Random(OperatorProcess process) {
		super("statistical.rand", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.REAL);
		this.process = process;
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length > 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), 0, 1,
					inputEvaluators.length);
		}
		ExpressionType resultType = getResultType(inputEvaluators);

		DoubleCallable doubleCallable;
		if (inputEvaluators.length == 1) {
			doubleCallable = makeDoubleCallable(inputEvaluators[0]);
		} else {
			// if no seed is passed use -1 to get global random generator
			doubleCallable = makeDoubleCallable(-1);
		}
		return new SimpleExpressionEvaluator(doubleCallable, resultType, false);
	}

	/**
	 * Creates a double callable. If the evaluator is constant this callable returns the next random
	 * of a random generator with the given seed. If the evaluator is not constant, it creates a new
	 * random generator with the changing seed for every call.
	 *
	 * @param evaluator
	 *            the evaluator that determines the random seed
	 * @return a double callable
	 */
	private DoubleCallable makeDoubleCallable(final ExpressionEvaluator evaluator) {
		try {
			if (evaluator.isConstant()) {
				// if the seed is fixed create one random generator with this seed
				return makeDoubleCallable((int) evaluator.getDoubleFunction().call());
			} else {
				// if the seed is not fixed create a new generator with this seed on every call
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						int seed = (int) evaluator.getDoubleFunction().call();
						RandomGenerator randomGenerator = RandomGenerator.getRandomGenerator(process, seed);
						return randomGenerator.nextDouble();
					}

				};
			}
		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}
	}

	/**
	 * Creates a {@link DoubleCallable} that calls the next random of a random generator with the
	 * given seed.
	 *
	 * @param seed
	 *            the seed, if negative the seed of the process root operator is used
	 * @return a double callable
	 */
	private DoubleCallable makeDoubleCallable(int seed) {
		final RandomGenerator randomGenerator = RandomGenerator.getRandomGenerator(process, seed);
		return new DoubleCallable() {

			@Override
			public double call() throws Exception {
				return randomGenerator.nextDouble();
			}

		};
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes.length > 0 && inputTypes[0] != ExpressionType.INTEGER) {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "integer");
		}
		return ExpressionType.DOUBLE;
	}

	@Override
	protected boolean isConstantOnConstantInput() {
		return false;
	}

}
