package io.sugo.pio.operator.learner.functions.linear;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.UndefinedParameterError;

import java.util.Collections;
import java.util.List;


/**
 * This method just does not perform any feature selection methods.
 * 
 * @author Sebastian Land
 */
public class PlainLinearRegressionMethod implements LinearRegressionMethod {

	@Override
	public LinearRegressionResult applyMethod(LinearRegression regression, boolean useBias, double ridge,
			ExampleSet exampleSet, boolean[] isUsedAttribute, int numberOfExamples, int numberOfUsedAttributes,
			double[] means, double labelMean, double[] standardDeviations, double labelStandardDeviation,
			double[] coefficientsOnFullData, double errorOnFullData) throws UndefinedParameterError {
		LinearRegressionResult result = new LinearRegressionResult();
		result.coefficients = coefficientsOnFullData;
		result.error = errorOnFullData;
		result.isUsedAttribute = isUsedAttribute;
		return result;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		return Collections.emptyList();
	}

}
