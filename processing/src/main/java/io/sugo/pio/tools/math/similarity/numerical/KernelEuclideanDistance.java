package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.tools.math.kernels.Kernel;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * This class uses the approach of Schoelkopf (2001) The Kernel Trick for Distances. It hence
 * calculates the distances between two examples in the transformed space defined by the chosen
 * kernel.
 * 
 */
public class KernelEuclideanDistance extends DistanceMeasure {

	private static final long serialVersionUID = 6764039884618489619L;
	private Kernel kernel;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return kernel.calculateDistance(value1, value1) + kernel.calculateDistance(value2, value2) - 2
				* kernel.calculateDistance(value1, value2);
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return -calculateDistance(value1, value2);
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler) throws OperatorException {
		super.init(exampleSet, parameterHandler);
		init(parameterHandler);
	}

	public void init(ParameterHandler handler) throws OperatorException {
		kernel = Kernel.createKernel(handler);
	}

	@Override
	public String toString() {
		return "Kernelspace euclidean distance";
	}
}
