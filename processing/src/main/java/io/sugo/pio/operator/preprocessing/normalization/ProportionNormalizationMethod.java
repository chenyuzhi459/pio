package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.Range;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 * A normalization method for bringing the sum of all attribute values to 1.
 * 
 */
public class ProportionNormalizationMethod extends AbstractNormalizationMethod {

	@Override
	public Collection<AttributeMetaData> modifyAttributeMetaData(ExampleSetMetaData emd, AttributeMetaData amd,
			InputPort exampleSetInputPort, ParameterHandler parameterHandler) throws UndefinedParameterError {
		if (amd.getValueSetRelation() == SetRelation.EQUAL) {
			if (emd.getNumberOfExamples().isKnown()) {
				amd.setMean(new MDReal(1d / emd.getNumberOfExamples().getValue()));
			} else {
				amd.setMean(new MDReal());
			}
			Range range = amd.getValueRange();
			if (range.getLower() < 0d) {
				exampleSetInputPort.addError(new SimpleMetaDataError(Severity.WARNING, exampleSetInputPort,
						"pio.error.metadata.attribute_contains_negative_values", amd.getName(), getName()));
			}
		} else {
			// set to unknown
			amd.setMean(new MDReal());
			amd.setValueRange(new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), SetRelation.UNKNOWN);
		}
		return Collections.singleton(amd);
	}

	@Override
	public AbstractNormalizationModel getNormalizationModel(ExampleSet exampleSet, Operator operator) {
		// calculating attribute sums
		Attributes attributes = exampleSet.getAttributes();
		double[] attributeSum = new double[attributes.size()];
		for (Example example : exampleSet) {
			int i = 0;
			for (Attribute attribute : attributes) {
				if (attribute.isNumerical()) {
					attributeSum[i] += example.getValue(attribute);
				}
				i++;
			}
		}
		HashMap<String, Double> attributeSums = new HashMap<String, Double>();
		int i = 0;
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				attributeSums.put(attribute.getName(), attributeSum[i]);
			}
			i++;
		}

		return new ProportionNormalizationModel(exampleSet, attributeSums);
	}

	@Override
	public String getName() {
		return "proportion transformation";
	}

	@Override
	public String getDisplayName() {
		return I18N.getMessage("pio.ProportionNormalizationMethod.display_name");
	}

}
