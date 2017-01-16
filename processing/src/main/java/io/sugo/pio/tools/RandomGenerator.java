package io.sugo.pio.tools;

import io.sugo.pio.OperatorProcess;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessRootOperator;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;

import java.util.*;

/**
 * The global random number generator. This should be used for all random purposes of RapidMiner to
 * ensure that two runs of the same process setup provide the same results.
 * 
 * @author Ralf Klinkenberg, Ingo Mierswa
 */
public class RandomGenerator extends Random {

	private static final long serialVersionUID = 7562534107359981433L;

	public static final String PARAMETER_USE_LOCAL_RANDOM_SEED = "use_local_random_seed";

	public static final String PARAMETER_LOCAL_RANDOM_SEED = "local_random_seed";

	/** Use this alphabet for random String creation. */
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	/**
	 * Global random number generator using the random number generator seed specified for the root
	 * operator (ProcessRootOperator).
	 */
	private static RandomGenerator globalRandomGenerator = new RandomGenerator(2001);

	/** Initializes the random number generator without a seed. */
	private RandomGenerator() {
		super();
	}

	/** Initializes the random number generator with the given <code>seed</code> */
	public RandomGenerator(long seed) {
		super(seed);
	}

	// ================================================================================

	/** Returns the global random number generator. */
	public static RandomGenerator getGlobalRandomGenerator() {
		return getRandomGenerator(null, -1);
	}

	// /** Returns the global random number generator if the seed is negative and a new
	// RandomGenerator
	// * with the given seed if the seed is positive or zero. This way is is possible to allow for
	// local
	// * random seeds. Operators like learners or validation operators should definitely make use of
	// such
	// * a local random generator. */
	// public static RandomGenerator getRandomGenerator(int seed) {
	// return getRandomGenerator(null, seed);
	// }

	/**
	 * Returns the global random number generator if useLocalGenerator is false and a new
	 * RandomGenerator with the given seed if the seed is positive or zero. This way is is possible
	 * to allow for local random seeds. Operators like learners or validation operators should
	 * definitely make use of such a local random generator.
	 */
	public static RandomGenerator getRandomGenerator(boolean useLocalGenerator, int localSeed) {
		return (useLocalGenerator) ? getRandomGenerator(null, localSeed) : getGlobalRandomGenerator();
	}

	/**
	 * Returns the global random number generator if the seed is negative and a new RandomGenerator
	 * with the given seed if the seed is positive or zero. This way is is possible to allow for
	 * local random seeds. Operators like learners or validation operators should definitely make use
	 * of such a local random generator.
	 */
	public static RandomGenerator getRandomGenerator(OperatorProcess process, int seed) {
		if (seed < 0) {
			if (globalRandomGenerator == null) { // might happen
				init(process);
			}
			return globalRandomGenerator;
		} else {
			return new RandomGenerator(seed);
		}
	}

	/**
	 * Instantiates the global random number generator and initializes it with the random number
	 * generator seed specified in the <code>global</code> section of the configuration file. Should
	 * be invoked before the process starts.
	 */
	public static void init(OperatorProcess process) {
		long seed = 2001;
		if (process != null) {
			try {
				seed = process.getRootOperator().getParameterAsInt(ProcessRootOperator.PARAMETER_RANDOM_SEED);
			} catch (UndefinedParameterError e) {
				// tries to read the general random seed
				// if no seed was specified (cannot happen) use seed 2001
				seed = 2001;
			}
		}
		if (seed == -1) {
			globalRandomGenerator = new RandomGenerator();
		} else {
			globalRandomGenerator = new RandomGenerator(seed);
		}
	}

	/**
	 * This method returns a list of parameters usable to conveniently provide parameters for random
	 * generator use within operators
	 * 
	 * @param operator
	 *            the operator
	 */
	public static List<ParameterType> getRandomGeneratorParameters(Operator operator) {
		List<ParameterType> types = new LinkedList<>();

		types.add(new ParameterTypeBoolean(PARAMETER_USE_LOCAL_RANDOM_SEED,
				"Indicates if a local random seed should be used.", false));

		ParameterType type = new ParameterTypeInt(PARAMETER_LOCAL_RANDOM_SEED, "Specifies the local random seed", 1,
				Integer.MAX_VALUE, 1992);
		type.registerDependencyCondition(new BooleanParameterCondition(operator, PARAMETER_USE_LOCAL_RANDOM_SEED, false,
				true));
		types.add(type);

		return types;
	}

	/**
	 * This method returns the appropriate RandomGenerator for the user chosen parameter combination
	 * 
	 * @param operator
	 * @throws UndefinedParameterError
	 */
	public static RandomGenerator getRandomGenerator(Operator operator) throws UndefinedParameterError {
		if (operator.getParameterAsBoolean(PARAMETER_USE_LOCAL_RANDOM_SEED)) {
			return new RandomGenerator(operator.getParameterAsInt(PARAMETER_LOCAL_RANDOM_SEED));
		} else {
			return globalRandomGenerator;
		}
	}

	// ================================================================================
	/**
	 * Returns the next pseudorandom, uniformly distributed <code>double</code> value between
	 * <code>lowerBound</code> and <code>upperBound</code> from this random number generator's
	 * sequence (exclusive of the interval endpoint values).
	 * 
	 * @throws IllegalArgumentException
	 *             if upperBound < lowerBound
	 */
	public double nextDoubleInRange(double lowerBound, double upperBound) throws IllegalArgumentException {
		if (upperBound < lowerBound) {
			throw new IllegalArgumentException("RandomGenerator.nextDoubleInRange : the upper bound of the "
					+ "random number range should be greater than the lower bound.");
		}
		return ((nextDouble() * (upperBound - lowerBound)) + lowerBound);
	}

	/**
	 * returns the next pseudorandom, uniformly distributed <code>long</code> value between
	 * <code>lowerBound</code> and <code>upperBound</code> from this random number generator's
	 * sequence (exclusive of the interval endpoint values).
	 */
	public long nextLongInRange(long lowerBound, long upperBound) {
		if (upperBound <= lowerBound) {
			throw new IllegalArgumentException("RandomGenerator.nextLongInRange : the upper bound of the "
					+ "random number range should be greater than the lower bound.");
		}
		return ((long) (nextDouble() * (upperBound - lowerBound + 1)) + lowerBound);
	}

	/**
	 * Returns the next pseudorandom, uniformly distributed <code>int</code> value between
	 * <code>lowerBound</code> and <code>upperBound</code> from this random number generator's
	 * sequence (lower bound inclusive, upper bound exclusive).
	 */
	public int nextIntInRange(int lowerBound, int upperBound) {
		if (upperBound <= lowerBound) {
			throw new IllegalArgumentException("RandomGenerator.nextIntInRange : the upper bound of the "
					+ "random number range should be greater than the lower bound.");
		}
		return nextInt(upperBound - lowerBound) + lowerBound;
	}

	/** Returns a random String of the given length. */
	public String nextString(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = ALPHABET.charAt(nextInt(ALPHABET.length()));
		}
		return new String(chars);
	}

	/**
	 * Returns a randomly selected integer between 0 and the length of the given array. Uses the
	 * given probabilities to determine the index, all values in this array must sum up to 1.
	 */
	public int randomIndex(double[] probs) {
		double r = nextDouble();
		double sum = 0.0d;
		for (int i = 0; i < probs.length; i++) {
			sum += probs[i];
			if (r < sum) {
				return i;
			}
		}
		return probs.length - 1;
	}

	/**
	 * This method returns a randomly filled array of given length
	 * 
	 * @param length
	 *            the length of the returned array
	 * @return the filled array
	 */
	public double[] nextDoubleArray(int length) {
		double[] values = new double[length];
		for (int i = 0; i < length; i++) {
			values[i] = nextDouble();
		}
		return values;
	}

	/** Returns a random date between the given ones. */
	public Date nextDateInRange(Date start, Date end) {
		return new Date(nextLongInRange(start.getTime(), end.getTime()));
	}

	/** Returns a set of integer within the given range and given size */
	public Set<Integer> nextIntSetWithRange(int lowerBound, int upperBound, int size) {
		if (upperBound <= lowerBound) {
			throw new IllegalArgumentException("RandomGenerator.nextIntInRange : the upper bound of the "
					+ "random number range should be greater than the lower bound.");
		}
		if ((upperBound - lowerBound) < size) {
			throw new IllegalArgumentException(
					"RandomGenerator.nextIntInRange : impossible to deliver the desired set of integeres --> range is too small.");
		}
		Set<Integer> set = new HashSet<>();
		while (set.size() < size) {
			set.add(nextIntInRange(lowerBound, upperBound));
		}
		return set;
	}
}
