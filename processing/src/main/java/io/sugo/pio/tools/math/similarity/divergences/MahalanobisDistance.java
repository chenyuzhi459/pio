package io.sugo.pio.tools.math.similarity.divergences;

import Jama.Matrix;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.matrix.CovarianceMatrix;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Mahalanobis distance &quot;.
 *
 */
public class MahalanobisDistance extends BregmanDivergence {

	private static final long serialVersionUID = -5986526237805285428L;
	private Matrix inverseCovariance;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {

		Matrix x = new Matrix(value1, value1.length);
		Matrix y = new Matrix(value2, value2.length);

		Matrix deltaxy = x.minus(y);

		// compute the mahalanobis distance
		return Math.sqrt(deltaxy.transpose().times(inverseCovariance).times(deltaxy).get(0, 0));
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
		inverseCovariance = CovarianceMatrix.getCovarianceMatrix(exampleSet, null).inverse();
	}

	@Override
	public String toString() {
		return "Mahalanobis distance";
	}
}
