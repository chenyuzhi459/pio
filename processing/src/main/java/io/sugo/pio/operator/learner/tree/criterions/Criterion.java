package io.sugo.pio.operator.learner.tree.criterions;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;

/**
 * The criterion for a splitted example set. Possible implementations are for example accuracy or
 * information gain.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public interface Criterion {

	public double getNominalBenefit(ExampleSet exampleSet, Attribute attribute) throws OperatorException;

	public double getNumericalBenefit(ExampleSet exampleSet, Attribute attribute, double splitValue)
			throws OperatorException;

	public boolean supportsIncrementalCalculation();

	public void startIncrementalCalculation(ExampleSet exampleSet) throws OperatorException;

	public void swapExample(Example example);

	public double getIncrementalBenefit();

	/**
	 * This method will return the calculated benefit if the weights would have distributed over the
	 * labels as given. The first index specifies the split fraction, the second index the label.
	 * Henve the first index is always between 0 and 1 included for numerical splits, since only two
	 * sides can occur there. For splits on nominal attributes, the number of split sides is
	 * determined by the number of possible values.
	 */
	public double getBenefit(double[][] weightCounts);
}
