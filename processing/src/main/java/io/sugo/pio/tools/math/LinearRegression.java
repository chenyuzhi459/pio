package io.sugo.pio.tools.math;

import Jama.Matrix;
import com.metamx.common.logger.Logger;

/**
 * This class can be used to calculate the coefficients of a (weighted) linear regression. It uses
 * the class Matrix from the Jama package for this purpose. It is also possible to apply Ridge
 * Regression which is a sort of regularization well suited especially for ill-posed problems.
 * Please note that for the dependent matrix Y only one column is allowed.
 * 
 * @author Ingo Mierswa
 */
public class LinearRegression {

	private static final Logger logger = new Logger(LinearRegression.class);

	/** Performs a weighted linear ridge regression. */
	public static double[] performRegression(Matrix x, Matrix y, double[] weights, double ridge) {
		Matrix weightedIndependent = new Matrix(x.getRowDimension(), x.getColumnDimension());
		Matrix weightedDependent = new Matrix(x.getRowDimension(), 1);
		for (int i = 0; i < weights.length; i++) {
			double sqrtWeight = Math.sqrt(weights[i]);
			for (int j = 0; j < x.getColumnDimension(); j++) {
				weightedIndependent.set(i, j, x.get(i, j) * sqrtWeight);
			}
			weightedDependent.set(i, 0, y.get(i, 0) * sqrtWeight);
		}
		return performRegression(weightedIndependent, weightedDependent, ridge);
	}

	/** Calculates the coefficients of linear ridge regression. */
	public static double[] performRegression(Matrix x, Matrix y, double ridge) {
		int numberOfColumns = x.getColumnDimension();
		double[] coefficients = new double[numberOfColumns];
		Matrix xTransposed = x.transpose();
		Matrix result;
		boolean finished = false;
		while (!finished) {
			Matrix xTx = xTransposed.times(x);

			for (int i = 0; i < numberOfColumns; i++) {
				xTx.set(i, i, xTx.get(i, i) + ridge);
			}

			Matrix xTy = xTransposed.times(y);
			for (int i = 0; i < numberOfColumns; i++) {
				coefficients[i] = xTy.get(i, 0);
			}

			try {
				result = xTx.solve(new Matrix(coefficients, coefficients.length));
				for (int i = 0; i < numberOfColumns; i++) {
					coefficients[i] = result.get(i, 0);
				}
				finished = true;
			} catch (Exception ex) {
				double ridgeOld = ridge;
				if (ridge > 0) {
					ridge *= 10;
				} else {
					ridge = 0.0000001;
				}
				finished = false;
				logger.warn("Error during calculation: " + ex.getMessage() + ": Increasing ridge factor from " + ridgeOld
						+ " to " + ridge);
			}
		}
		return coefficients;
	}
}
