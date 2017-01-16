package io.sugo.pio.example.set;

/**
 * Creates partitions from ratio arrays. Subclasses might shuffle the examples before building the
 * partition or apply stratification. The delivered partitions consist of an array of integer values
 * with the same length as the given size. For each element the integer defines the number of the
 * partition for this element. Numbering starts with 0.
 * 
 * @author Ingo Mierswa
 */
public interface PartitionBuilder {

	/**
	 * Creates a partition from the given ratios. Size is the number of elements, i.e. the number of
	 * examples.
	 */
	public int[] createPartition(double[] ratio, int size);
}
