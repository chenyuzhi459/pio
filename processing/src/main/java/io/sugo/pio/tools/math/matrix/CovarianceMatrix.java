package io.sugo.pio.tools.math.matrix;

import Jama.Matrix;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.ProcessStoppedException;


/**
 * This helper class can be used to calculate a covariance matrix from given matrices or example
 * sets.
 *
 */
public class CovarianceMatrix {

	/**
	 * Transforms the example set into a double matrix (doubling the amount of used memory) and
	 * invokes {@link #getCovarianceMatrix(double[][])}.
	 *
	 * @deprecated Please use {@link #getCovarianceMatrix(ExampleSet, Operator)} so that the
	 *             calculation can be stopped if necessary.
	 */
	@Deprecated
	public static Matrix getCovarianceMatrix(ExampleSet exampleSet) {
		Matrix matrix = new Matrix(0, 0);
		try {
			matrix = CovarianceMatrix.getCovarianceMatrix(exampleSet, null);
		} catch (ProcessStoppedException e) {
			// This Exception is impossible as long the parameter op is null
		}
		return matrix;
	}

	/**
	 * Transforms the example set into a double matrix (doubling the amount of used memory) and
	 * invokes {@link #getCovarianceMatrix(double[][])}.
	 *
	 * @param exampleSet
	 *            ExampleSet to construct the covariance matrix from
	 * @param op
	 *            executing Operator which will be used to check for stop (can be null).
	 *
	 * @throws ProcessStoppedException
	 */
	public static Matrix getCovarianceMatrix(ExampleSet exampleSet, Operator op) throws ProcessStoppedException {
		boolean checkForStop = op != null;
		double[][] data = new double[exampleSet.size()][exampleSet.getAttributes().size()];
		int r = 0;
		for (Example example : exampleSet) {
			int c = 0;
			for (Attribute attribute : exampleSet.getAttributes()) {
				data[r][c] = example.getValue(attribute);
				c++;
			}
			r++;

			if (checkForStop) {
				op.checkForStop();
			}
		}

		return getCovarianceMatrix(data, op);
	}

	/**
	 * Returns the covariance matrix from the given double matrix.
	 *
	 * @deprecated Please use {@link #getCovarianceMatrix(double[][], Operator)} so that the
	 *             calculation can be stopped if necessary.
	 */
	@Deprecated
	public static Matrix getCovarianceMatrix(double[][] data) {
		Matrix matrix = new Matrix(0, 0);
		try {
			matrix = CovarianceMatrix.getCovarianceMatrix(data, null);
		} catch (ProcessStoppedException e) {
			// This Exception is impossible as long the parameter op is null
		}
		return matrix;
	}

	/**
	 * Returns the covariance matrix from the given double matrix.
	 *
	 * @param data
	 *            data to construct the covariance matrix from
	 * @param op
	 *            executing Operator which will be used to check for stop (can be null).
	 */
	public static Matrix getCovarianceMatrix(double[][] data, Operator op) throws ProcessStoppedException {
		// checks
		if (data.length == 0) {
			throw new IllegalArgumentException(
					"Calculation of covariance matrices not possible for data sets with zero rows.");
		}

		int numberOfColumns = -1;
		for (int r = 0; r < data.length; r++) {
			if (numberOfColumns < 0) {
				numberOfColumns = data[r].length;
				if (numberOfColumns <= 0) {
					throw new IllegalArgumentException(
							"Calculation of covariance matrices not possible for data sets with zero columns.");
				}
			} else {
				if (numberOfColumns != data[r].length) {
					throw new IllegalArgumentException(
							"Calculation of covariance matrices not possible for data sets with different numbers of columns.");
				}
			}
		}

		// check whether a operator is executing this
		boolean checkForStop = op != null;

		// subtract column-averages
		for (int c = 0; c < numberOfColumns; c++) {
			double average = getAverageForColumn(data, c);
			for (int r = 0; r < data.length; r++) {
				data[r][c] -= average;
			}
		}

		// create covariance matrix
		double[][] covarianceMatrixEntries = new double[numberOfColumns][numberOfColumns];

		// fill the covariance matrix
		for (int i = 0; i < covarianceMatrixEntries.length; i++) {
			if (checkForStop) {
				op.checkForStop();
			}
			for (int j = i; j < covarianceMatrixEntries.length; j++) {
				double covariance = getCovariance(data, i, j);
				covarianceMatrixEntries[i][j] = covariance;
				covarianceMatrixEntries[j][i] = covariance;
			}
		}

		return new Matrix(covarianceMatrixEntries);
	}

	/** Returns the average for the column with the given index. */
	private static double getAverageForColumn(double[][] data, int column) {
		double sum = 0.0d;
		for (int r = 0; r < data.length; r++) {
			sum += data[r][column];
		}
		return sum / data.length;
	}

	/** Returns the covariance between the given columns. */
	private static double getCovariance(double[][] data, int x, int y) {
		double cov = 0;
		for (int i = 0; i < data.length; i++) {
			cov += data[i][x] * data[i][y];
		}
		return cov / (data.length - 1);
	}
}
