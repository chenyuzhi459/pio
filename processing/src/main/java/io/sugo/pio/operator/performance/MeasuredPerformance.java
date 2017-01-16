/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.performance;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;

/**
 */
public abstract class MeasuredPerformance extends PerformanceCriterion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4465054472762456363L;

	public MeasuredPerformance() {}

	public MeasuredPerformance(MeasuredPerformance o) {
		super(o);
	}

	/** Counts a single example, e.g. by summing up errors. */
	public abstract void countExample(Example example);

	/**
	 * Initialized the criterion. The default implementation invokes the initialization method with
	 * useExampleWeights set to true.
	 * 
	 * @deprecated Please use the other start counting method directly
	 */
	@Deprecated
	public final void startCounting(ExampleSet set) throws OperatorException {
		startCounting(set, true);
	}

	/** Initializes the criterion. The default implementation does nothing. */
	public void startCounting(ExampleSet set, boolean useExampleWeights) throws OperatorException {}

}
