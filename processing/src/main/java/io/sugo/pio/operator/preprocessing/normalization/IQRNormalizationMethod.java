package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 * This is the normalization method for interquartile range.
 * 
 */
public class IQRNormalizationMethod extends AbstractNormalizationMethod {

	@Override
	public Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd,
			InputPort exampleSetInputPort, ParameterHandler parameterHandler) throws UndefinedParameterError {
		amd.setMean(new MDReal((double) 0));
		amd.setValueRange(new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), SetRelation.SUBSET);
		return Collections.singleton(amd);
	}

	@Override
	public AbstractNormalizationModel getNormalizationModel(ExampleSet exampleSet, Operator operator) throws UserError {
		// IQR Transformation
		IQRNormalizationModel model = new IQRNormalizationModel(exampleSet, calculateMeanSigma(exampleSet));
		return model;
	}

	private HashMap<String, Tupel<Double, Double>> calculateMeanSigma(ExampleSet exampleSet) {
		HashMap<String, Tupel<Double, Double>> attributeMeanSigmaMap = new HashMap<String, Tupel<Double, Double>>();

		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				double values[] = new double[exampleSet.size()];
				int i = 0;
				for (Example example : exampleSet) {
					values[i++] = example.getValue(attribute);
				}

				Arrays.sort(values);

				int lowerQuart = (int) (((values.length + 1) * 0.25) - 1);
				int upperQuart = (int) (((values.length + 1) * 0.75) - 1);

				double iqSigma = (values[upperQuart] - values[lowerQuart]) / 1.349;
				double median = 0;

				if (0 == (exampleSet.size() % 2)) {
					if (exampleSet.size() > 1) {
						median = (values[exampleSet.size() / 2] + values[(exampleSet.size() / 2) - 1]) / 2;
					}
				} else {
					median = values[exampleSet.size() / 2];
				}

				attributeMeanSigmaMap.put(attribute.getName(), new Tupel<Double, Double>(median, iqSigma));
			}
		}
		return attributeMeanSigmaMap;
	}

	@Override
	public String getName() {
		return "interquartile range";
	}

	@Override
	public String getDisplayName() {
		return I18N.getMessage("pio.IQRNormalizationMethod.display_name");
	}

}
