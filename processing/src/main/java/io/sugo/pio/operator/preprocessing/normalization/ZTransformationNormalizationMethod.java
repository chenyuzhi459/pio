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
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.container.Tupel;
import io.sugo.pio.tools.Range;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 * The normalization method for the Z-Transformation
 * 
 */
public class ZTransformationNormalizationMethod extends AbstractNormalizationMethod {

	@Override
	public Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd,
			InputPort exampleSetInputPort, ParameterHandler parameterHandler) throws UndefinedParameterError {
		amd.setMean(new MDReal((double) 0));
		amd.setValueRange(new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), SetRelation.SUBSET);
		return Collections.singleton(amd);
	}

	@Override
	public AbstractNormalizationModel getNormalizationModel(ExampleSet exampleSet, Operator operator) throws UserError {
		// Z-Transformation
		exampleSet.recalculateAllAttributeStatistics();
		HashMap<String, Tupel<Double, Double>> attributeMeanVarianceMap = new HashMap<String, Tupel<Double, Double>>();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				attributeMeanVarianceMap.put(
						attribute.getName(),
						new Tupel<Double, Double>(exampleSet.getStatistics(attribute, Statistics.AVERAGE), exampleSet
								.getStatistics(attribute, Statistics.VARIANCE)));
			}
		}
		ZTransformationModel model = new ZTransformationModel(exampleSet, attributeMeanVarianceMap);
		return model;
	}

	@Override
	public String getName() {
		return "Z-transformation";
	}

	@Override
	public String getDisplayName() {
		return I18N.getMessage("pio.ZTransformationNormalizationMethod.display_name");
	}

}
