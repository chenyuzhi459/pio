package io.sugo.pio.operator.preprocessing;

import io.sugo.pio.PioMiner;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeRole;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.DataRow;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.table.NominalMapping;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.example.util.ExampleSetBuilder;
import io.sugo.pio.example.util.ExampleSets;
import io.sugo.pio.operator.io.ExampleSource;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.tools.ParameterService;

import java.util.Iterator;
import java.util.List;

/**
 * Creates a fresh and clean copy of the data in memory. Might be very useful in combination with
 * the {@link MemoryCleanUp} operator after large preprocessing trees using lot of views or data
 * copies.
 *
 * @author Ingo Mierswa
 */
public class MaterializeDataInMemory extends AbstractDataProcessing {

	public MaterializeDataInMemory(String name) {
		super("materializeDataInMemory");
	}

	@Override
	public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
		ExampleSet createdSet = materializeExampleSet(exampleSet, getParameterAsInt(ExampleSource.PARAMETER_DATAMANAGEMENT));
		return createdSet;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeCategory(ExampleSource.PARAMETER_DATAMANAGEMENT,
				"Determines, how the data is represented internally.", DataRowFactory.TYPE_NAMES,
				DataRowFactory.TYPE_DOUBLE_ARRAY));
		return types;
	}

//	@Override
//	public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
//		return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(),
//				MaterializeDataInMemory.class, null);
//	}

	/**
	 * Creates a materialized copy of the given example set, i.e., a hard copy with all unnecessary
	 * abstraction layers being removed. The data management strategy will be the same as in the
	 * current example set. If you want to use a different strategy call
	 * {@link #materializeExampleSet(ExampleSet, int)} instead.
	 *
	 * @param exampleSet
	 *            the example set to materialize
	 * @return the materialized example set
	 * @throws UndefinedParameterError
	 */
	public static ExampleSet materializeExampleSet(ExampleSet exampleSet) {
		return materialize(exampleSet, findDataRowType(exampleSet));
	}

	/**
	 * Creates a materialized copy of the given example set, i.e., a hard copy with all unnecessary
	 * abstraction layers being removed.
	 *
	 * @param exampleSet
	 *            the example set to materialize
	 * @param dataManagement
	 *            the data management strategy (see {@link DataRowFactory} for available types)
	 * @return the materialized example set
	 * @throws UndefinedParameterError
	 */
	public static ExampleSet materializeExampleSet(ExampleSet exampleSet, int dataManagement)
			throws UndefinedParameterError {
		return materialize(exampleSet, dataManagement);
	}

	/**
	 * Creates a materialized copy of the given example set, i.e., a hard copy with all unnecessary
	 * abstraction layers being removed.
	 *
	 * @param exampleSet
	 *            the example set to materialize
	 * @param dataManagement
	 *            the data management strategy
	 * @return the materialized example set
	 */
	private static ExampleSet materialize(ExampleSet exampleSet, int dataManagement) {
		// create new attributes
		Attribute[] sourceAttributes = new Attribute[exampleSet.getAttributes().allSize()];
		Attribute[] targetAttributes = new Attribute[exampleSet.getAttributes().allSize()];
		String[] targetRoles = new String[targetAttributes.length];

		Iterator<AttributeRole> iterator = exampleSet.getAttributes().allAttributeRoles();
		for (int i = 0; i < sourceAttributes.length; i++) {
			AttributeRole sourceRole = iterator.next();
			sourceAttributes[i] = sourceRole.getAttribute();
			targetAttributes[i] = AttributeFactory.createAttribute(sourceAttributes[i].getName(),
					sourceAttributes[i].getValueType());

			if (sourceAttributes[i].isNominal()) {
				targetAttributes[i].setMapping((NominalMapping) sourceAttributes[i].getMapping().clone());
			}

			if (sourceRole.isSpecial()) {
				targetRoles[i] = sourceRole.getSpecialName();
			}

			targetAttributes[i].getAnnotations().addAll(sourceAttributes[i].getAnnotations());
		}

		// size table by setting number of rows and add attributes
		ExampleSetBuilder builder = ExampleSets.from(targetAttributes);

		// copy columnwise if beta features are activated and dataManagment is double array or
		// column view
		// if datamanagment is not one of the two then there can be value changes when copying to a
		// "smaller" row which we need to keep
		if (Boolean.valueOf(ParameterService.getParameterValue(PioMiner.PROPERTY_RAPIDMINER_UPDATE_BETA_FEATURES))
				&& (dataManagement == DataRowFactory.TYPE_DOUBLE_ARRAY
						|| dataManagement == DataRowFactory.TYPE_COLUMN_VIEW)) {
			builder.withBlankSize(exampleSet.size());
			for (int i = 0; i < sourceAttributes.length; i++) {
				final int index = i;
				builder.withColumnFiller(targetAttributes[i],
						j -> exampleSet.getExample(j).getValue(sourceAttributes[index]));
			}
		} else {
			builder.withExpectedSize(exampleSet.size());
			DataRowFactory rowFactory = new DataRowFactory(dataManagement, '.');

			// copying data differently for sparse and non sparse for speed reasons
			if (isSparseType(dataManagement)) {
				for (Example example : exampleSet) {
					DataRow targetRow = rowFactory.create(targetAttributes.length);
					for (int i = 0; i < sourceAttributes.length; i++) {
						double value = example.getValue(sourceAttributes[i]);
						// we have a fresh sparse row, so everything is currently empty and we only
						// need to set non default value attributes to avoid unnecessary binary
						// searchs
						if (value != 0) {
							targetRow.set(targetAttributes[i], value);
						}
					}
					builder.addDataRow(targetRow);
				}
			} else {
				// dense data we copy entirely without condition
				for (Example example : exampleSet) {
					DataRow targetRow = rowFactory.create(targetAttributes.length);
					for (int i = 0; i < sourceAttributes.length; i++) {
						targetRow.set(targetAttributes[i], example.getValue(sourceAttributes[i]));
					}
					builder.addDataRow(targetRow);
				}
			}
		}

		// create and return result
		for (int i = 0; i < targetAttributes.length; i++) {
			builder.withRole(targetAttributes[i], targetRoles[i]);
		}
		ExampleSet createdSet = builder.build();
		createdSet.getAnnotations().addAll(exampleSet.getAnnotations());
		return createdSet;
	}

	/**
	 * Returns whether the given type is sparse.
	 */
	private static boolean isSparseType(int dataRowType) {
		switch (dataRowType) {
			case DataRowFactory.TYPE_BOOLEAN_SPARSE_ARRAY:
			case DataRowFactory.TYPE_BYTE_SPARSE_ARRAY:
			case DataRowFactory.TYPE_DOUBLE_SPARSE_ARRAY:
			case DataRowFactory.TYPE_FLOAT_SPARSE_ARRAY:
			case DataRowFactory.TYPE_INT_SPARSE_ARRAY:
			case DataRowFactory.TYPE_LONG_SPARSE_ARRAY:
			case DataRowFactory.TYPE_SHORT_SPARSE_ARRAY:
			case DataRowFactory.TYPE_SPARSE_MAP:
				return true;
			default:
				return false;
		}
	}

	/**
	 * This method determines the current used data row implementation in RapidMiner's backend.
	 */
	private static int findDataRowType(ExampleSet exampleSet) {
		if (exampleSet.size() > 0) {
			// then determine current representation: get first row
			DataRow usedRow = exampleSet.getExample(0).getDataRow();
			if (usedRow != null) {
				return usedRow.getType();
			}
		}
		// default type
		return DataRowFactory.TYPE_DOUBLE_ARRAY;
	}

	@Override
	public boolean writesIntoExistingData() {
		return false;
	}
}
