package io.sugo.pio.example.table;

/**
 * This interface defines methods for sparse data rows which might be implemented to allow more
 * efficient querying of the non-default values. Please refer to {@link FastExample2SparseTransform}
 * for further information.
 * 
 * @author Ingo Mierswa
 */
public interface SparseDataRow {

	/**
	 * Returns an array of all attribute indices with corresponding non-default values.
	 */
	public int[] getNonDefaultIndices();

	/** Returns an array of all non-default attribute values. */
	public double[] getNonDefaultValues();
}
