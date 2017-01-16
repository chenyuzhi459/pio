package io.sugo.pio.operator.learner;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorProgress;

import java.util.Iterator;

/**
 * A model that can be applied to an example set by applying it to each example separately. Just as
 * for the usual prediction model, subclasses must provide a constructor getting a label attribute
 * which will be used to invoke the super one-argument constructor.
 *
 * @author Ingo Mierswa, Simon Fischer ingomierswa Exp $
 */
public abstract class SimplePredictionModel extends PredictionModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 6275902545494306001L;

	private static final int OPERATOR_PROGRESS_STEPS = 1000;

	/**
	 *
	 * @param sizeCompareOperator
	 *            describes the allowed relations between the given ExampleSet and future
	 *            ExampleSets on which this Model will be applied. If this parameter is null no
	 *            error will be thrown and no check will be done.
	 * @param typeCompareOperator
	 *            describes the allowed relations between the types of the attributes of the given
	 *            ExampleSet and the types of future attributes of ExampleSet on which this Model
	 *            will be applied. If this parameter is null no error will be thrown and no check
	 *            will be done.
	 */
	protected SimplePredictionModel(ExampleSet exampleSet, ExampleSetUtilities.SetsCompareOption sizeCompareOperator,
									ExampleSetUtilities.TypesCompareOption typeCompareOperator) {
		super(exampleSet, sizeCompareOperator, typeCompareOperator);
	}

	/**
	 * Applies the model to a single example and returns the predicted class value.
	 */
	public abstract double predict(Example example) throws OperatorException;

	/** Iterates over all examples and applies the model to them. */
	@Override
	public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
		Iterator<Example> r = exampleSet.iterator();
		OperatorProgress progress = null;
		if (getShowProgress() && getOperator() != null && getOperator().getProgress() != null) {
			progress = getOperator().getProgress();
			progress.setTotal(exampleSet.size());
		}
		int progressCounter = 0;

		while (r.hasNext()) {
			Example example = r.next();
			example.setValue(predictedLabel, predict(example));
			if (progress != null && ++progressCounter % OPERATOR_PROGRESS_STEPS == 0) {
				progress.setCompleted(progressCounter);
			}
		}
		return exampleSet;
	}
}
