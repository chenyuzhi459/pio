package io.sugo.pio.operator.io;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.GrowingExampleTable;
import io.sugo.pio.example.table.MemoryExampleTable;
import io.sugo.pio.example.table.column.ColumnarExampleTable;
import io.sugo.pio.tools.ParameterService;

import java.util.List;

/**
 * This class consists exclusively of static methods that help to build new {@link ExampleSet}s.
 *
 * @author Gisa Schaefer
 * @since 7.3
 */
public final class ExampleSets {

	private ExampleSets() {}

	/**
	 * Creates a builder for an {@link ExampleSet} starting from the given attributes. If the given
	 * attributes are {@code null}, the example will have no attributes.
	 *
	 * @param attributes
	 *            the attributes for the new {@link ExampleSet}, can be {@code null}
	 * @return the {@link ExampleSetBuilder} to build the example set
	 */
	public static ExampleSetBuilder from(List<Attribute> attributes) {
//		if (Boolean.valueOf(ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_UPDATE_BETA_FEATURES))) {
//			return new ColumnarExampleSetBuilder(attributes);
//		} else {
//			return new MemoryExampleSetBuilder(attributes);
//		}
		return new MemoryExampleSetBuilder(attributes);
	}

	/**
	 * Creates a builder for an {@link ExampleSet} starting from the given attributes.
	 *
	 * @param attributes
	 *            the attributes for the new {@link ExampleSet}
	 * @return the {@link ExampleSetBuilder} to build the example set
	 */
	public static ExampleSetBuilder from(Attribute... attributes) {
//		if (Boolean.valueOf(ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_UPDATE_BETA_FEATURES))) {
//			return new ColumnarExampleSetBuilder(attributes);
//		} else {
//			return new MemoryExampleSetBuilder(attributes);
//		}
		return new MemoryExampleSetBuilder(attributes);
	}

	/**
	 * Creates an {@link ExampleTable} to which rows can be added. Only use this if it is not
	 * possible to use an {@link ExampleSetBuilder}.
	 *
	 * @param attributes
	 *            the attributes for the new {@link ExampleTable}
	 * @return a table that can grow
	 */
	@SuppressWarnings("deprecation")
	public static GrowingExampleTable createTableFrom(List<Attribute> attributes) {
//		if (Boolean.valueOf(ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_UPDATE_BETA_FEATURES))) {
//			return new ColumnarExampleTable(attributes);
//		} else {
//			return new MemoryExampleTable(attributes);
//		}
		return new MemoryExampleTable(attributes);
	}
}
