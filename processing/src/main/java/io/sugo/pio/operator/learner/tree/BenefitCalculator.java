package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.core.concurrency.ConcurrencyContext;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.tree.criterions.ColumnCriterion;
import io.sugo.pio.studio.internal.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Used to calculate the benefit for splitting at a certain attribute.
 *
 * @author Gisa Schaefer
 *
 */
public class BenefitCalculator {

	private Operator operator;

	private ColumnExampleTable columnTable;

	private ColumnCriterion criterion;

	private ColumnNumericalSplitter splitter;

	public BenefitCalculator(ColumnExampleTable columnTable, ColumnCriterion criterion, Operator operator) {
		this.columnTable = columnTable;
		this.criterion = criterion;
		this.operator = operator;
		splitter = new ColumnNumericalSplitter(columnTable, criterion);
	}

	/**
	 * This method calculates the benefit of the given attribute. This implementation utilizes the
	 * defined {@link Criterion}.
	 */
	private ParallelBenefit calculateBenefit(Map<Integer, int[]> allSelectedExamples, int attributeNumber) {
		if (columnTable.representsNominalAttribute(attributeNumber)) {
			return new ParallelBenefit(criterion.getNominalBenefit(columnTable,
					SelectionCreator.getArbitraryValue(allSelectedExamples), attributeNumber), attributeNumber);
		} else {
			// numerical attribute
			int[] selectedExamples = allSelectedExamples.get(attributeNumber);
			return splitter.getBestSplitBenefit(selectedExamples, attributeNumber);
		}
	}

	/**
	 * Calculates the benefits for all selected attributes on the given selected examples in
	 * parallel.
	 *
	 * @param allSelectedExamples
	 * @param selectedAttributes
	 * @return
	 * @throws OperatorException
	 */
	public List<ParallelBenefit> calculateAllBenefitsParallel(final Map<Integer, int[]> allSelectedExamples,
			final int[] selectedAttributes) throws OperatorException {
		ConcurrencyContext context = Resources.getConcurrencyContext(operator);

		final Vector<ParallelBenefit> benefits = new Vector<ParallelBenefit>();
		final int numberOfParallel = Math.min(context.getParallelism(), selectedAttributes.length);
		List<Callable<Void>> tasks = new ArrayList<>(numberOfParallel);

		for (int i = 0; i < numberOfParallel; i++) {
			final int counter = i;
			Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() {
					for (int j = counter; j < selectedAttributes.length; j += numberOfParallel) {

						int attribute = selectedAttributes[j];
						ParallelBenefit currentBenefit = calculateBenefit(allSelectedExamples, attribute);
						if (currentBenefit != null) {
							benefits.add(currentBenefit);
						}

					}
					return null;
				}
			};
			tasks.add(task);
		}

		try {
			context.call(tasks);
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new OperatorException(cause.getMessage(), cause);
			}

		}
		return benefits;
	}

	/**
	 * Calculates the benefits for all selected attributes on the given selected examples.
	 *
	 * @param allSelectedExamples
	 * @param selectedAttributes
	 * @return
	 */
	public List<ParallelBenefit> calculateAllBenefits(Map<Integer, int[]> allSelectedExamples, int[] selectedAttributes) {
		Vector<ParallelBenefit> benefits = new Vector<ParallelBenefit>();

		for (int attribute : selectedAttributes) {
			ParallelBenefit currentBenefit = calculateBenefit(allSelectedExamples, attribute);
			if (currentBenefit != null) {
				benefits.add(currentBenefit);
			}
		}

		return benefits;
	}

}