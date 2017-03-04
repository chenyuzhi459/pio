package io.sugo.pio.tools.math.matrix;

import Jama.Matrix;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;


/**
 * This is a class containing general methods for matrices.
 * 
 */
public class MatrixTools {

	/**
	 * This method copies the complete data of the exampleSet into an Matrix backed by an array.
	 */
	public static final Matrix getDataAsMatrix(ExampleSet exampleSet) {
		Attributes attributes = exampleSet.getAttributes();
		double[][] data = new double[exampleSet.size()][attributes.size()];
		int r = 0;
		for (Example example : exampleSet) {
			int c = 0;
			for (Attribute attribute : attributes) {
				data[r][c] = example.getValue(attribute);
				c++;
			}
			r++;
		}
		return new Matrix(data);
	}
}
