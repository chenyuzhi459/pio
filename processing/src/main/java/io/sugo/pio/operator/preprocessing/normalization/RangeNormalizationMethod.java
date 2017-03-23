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
package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MDReal;
import io.sugo.pio.ports.metadata.SetRelation;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.container.Tupel;
import io.sugo.pio.tools.Range;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * This is the transformation method for transforming the data of an attribute into a certain
 * interval.
 * 
 */
public class RangeNormalizationMethod extends AbstractNormalizationMethod {

	/** The parameter name for &quot;The minimum value after normalization&quot; */
	public static final String PARAMETER_MIN = "min";

	/** The parameter name for &quot;The maximum value after normalization&quot; */
	public static final String PARAMETER_MAX = "max";

	@Override
	public Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd,
			InputPort exampleSetInputPort, ParameterHandler parameterHandler) throws UndefinedParameterError {
		double min = parameterHandler.getParameterAsDouble(PARAMETER_MIN);
		double max = parameterHandler.getParameterAsDouble(PARAMETER_MAX);
		amd.setMean(new MDReal());
		amd.setValueRange(new Range(min, max), SetRelation.EQUAL);
		return Collections.singleton(amd);
	}

	@Override
	public AbstractNormalizationModel getNormalizationModel(ExampleSet exampleSet, Operator operator) throws UserError {
		// Range Normalization
		double min = operator.getParameterAsDouble(PARAMETER_MIN);
		double max = operator.getParameterAsDouble(PARAMETER_MAX);
		if (max <= min) {
			throw new UserError(operator, "pio.error.illegal_param_value", "max", "Must be greater than 'min'");
		}

		// calculating attribute ranges
		HashMap<String, Tupel<Double, Double>> attributeRanges = new HashMap<String, Tupel<Double, Double>>();
		exampleSet.recalculateAllAttributeStatistics();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				attributeRanges.put(
						attribute.getName(),
						new Tupel<Double, Double>(exampleSet.getStatistics(attribute, Statistics.MINIMUM), exampleSet
								.getStatistics(attribute, Statistics.MAXIMUM)));
			}
		}
		return new MinMaxNormalizationModel(exampleSet, min, max, attributeRanges);
	}

	@Override
	public String getName() {
		return "range transformation";
	}

	@Override
	public String getDisplayName() {
		return I18N.getMessage("pio.RangeNormalizationMethod.display_name");
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler handler) {
		List<ParameterType> types = super.getParameterTypes(handler);
		types.add(new ParameterTypeDouble(PARAMETER_MIN, I18N.getMessage("pio.RangeNormalizationMethod.min"), Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, 0.0d));
		types.add(new ParameterTypeDouble(PARAMETER_MAX, I18N.getMessage("pio.RangeNormalizationMethod.max"), Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, 1.0d));
		return types;
	}
}
