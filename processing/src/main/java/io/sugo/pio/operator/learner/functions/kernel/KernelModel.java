package io.sugo.pio.operator.learner.functions.kernel;

import io.sugo.pio.datatable.DataTable;
import io.sugo.pio.datatable.SimpleDataTable;
import io.sugo.pio.datatable.SimpleDataTableRow;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.example.set.HeaderExampleSet;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.tools.Tools;


/**
 * This is the abstract model class for all kernel models. This class actually only provide a common
 * interface for plotting SVM and other kernel method models.
 *
 */
public abstract class KernelModel extends PredictionModel {

	private static final long serialVersionUID = 7480153570564620067L;

	private final String[] attributeConstructions;

	/**
	 * Creates a new {@link KernelModel} which was built on the given example set. Please note that
	 * the given example set is automatically transformed into a {@link HeaderExampleSet} which
	 * means that no reference to the data itself is kept but only to the header, i.e. to the
	 * attribute meta descriptions.
	 *
	 * @deprecated Since RapidMiner Studio 6.0.009. Please use the new Constructor
	 *             {@link #KernelModel(ExampleSet, io.sugo.pio.example.set.ExampleSetUtilities.SetsCompareOption, io.sugo.pio.example.set.ExampleSetUtilities.TypesCompareOption)}
	 *             which offers the possibility to check for AttributeType and kind of ExampleSet
	 *             before execution.
	 */
	@Deprecated
	public KernelModel(ExampleSet exampleSet) {
		this(exampleSet, null, null);
	}

	/**
	 * Creates a new {@link KernelModel} which is build based on the given {@link ExampleSet}.
	 * Please note that the given ExampleSet is automatically transformed into a
	 * {@link HeaderExampleSet} which means that no reference to the data itself is kept but only to
	 * the header, i.e., to the attribute meta descriptions.
	 *
	 * @param sizeCompareOperator
	 *            describes the allowed relations between the given ExampleSet and future
	 *            ExampleSets on which this Model will be applied. If this parameter is null no
	 *            error will be thrown.
	 * @param typeCompareOperator
	 *            describes the allowed relations between the types of the attributes of the given
	 *            ExampleSet and the types of future attributes of ExampleSet on which this Model
	 *            will be applied. If this parameter is null no error will be thrown.
	 */
	public KernelModel(ExampleSet exampleSet, ExampleSetUtilities.SetsCompareOption sizeCompareOperator,
                       ExampleSetUtilities.TypesCompareOption typeCompareOperator) {
		super(exampleSet, sizeCompareOperator, typeCompareOperator);
		this.attributeConstructions = io.sugo.pio.example.Tools.getRegularAttributeConstructions(exampleSet);
	}

	public abstract double getBias();

	public abstract double getAlpha(int index);

	public abstract double getFunctionValue(int index);

	public abstract boolean isClassificationModel();

	public abstract String getClassificationLabel(int index);

	public abstract double getRegressionLabel(int index);

	public abstract String getId(int index);

	public abstract SupportVector getSupportVector(int index);

	public abstract int getNumberOfSupportVectors();

	public abstract int getNumberOfAttributes();

	public abstract double getAttributeValue(int exampleIndex, int attributeIndex);

	public String[] getAttributeConstructions() {
		return this.attributeConstructions;
	}

	/** The default implementation returns the classname without package. */
	@Override
	public String getName() {
		return "Kernel Model";
	}

	/** Returns a string representation of this model. */
	@Override
	public String toString() {
		String[] attributeNames = io.sugo.pio.example.Tools.getRegularAttributeNames(getTrainingHeader());

		StringBuffer result = new StringBuffer();
		result.append("Total number of Support Vectors: " + getNumberOfSupportVectors() + Tools.getLineSeparator());
		result.append("Bias (offset): " + Tools.formatNumber(getBias()) + Tools.getLineSeparators(2));
		if (!getLabel().isNominal() || getLabel().getMapping().size() == 2) {
			double[] w = new double[getNumberOfAttributes()];
			boolean showWeights = true;
			for (int i = 0; i < getNumberOfSupportVectors(); i++) {
				SupportVector sv = getSupportVector(i);
				if (sv != null) {
					double[] x = sv.getX();
					double alpha = sv.getAlpha();
					double y = sv.getY();
					for (int j = 0; j < w.length; j++) {
						w[j] += y * alpha * x[j];
					}
				} else {
					showWeights = false;
				}
			}
			if (showWeights) {
				for (int j = 0; j < w.length; j++) {
					result.append("w["
							+ attributeNames[j]
									+ (!attributeNames[j].equals(attributeConstructions[j]) ? " = " + attributeConstructions[j] : "")
									+ "] = " + Tools.formatNumber(w[j]) + Tools.getLineSeparator());
				}
			}
		} else {
			result.append("Feature weight calculation only possible for two class learning problems."
					+ Tools.getLineSeparator() + "Please use the operator SVMWeighting instead." + Tools.getLineSeparator());
		}
		return result.toString();
	}

	public DataTable createWeightsTable() {
		String[] attributeNames = io.sugo.pio.example.Tools.getRegularAttributeNames(getTrainingHeader());

		SimpleDataTable weightTable = new SimpleDataTable("Kernel Model Weights", new String[] { "Attribute", "Weight" });
		if (!getLabel().isNominal() || getLabel().getMapping().size() == 2) {
			double[] w = new double[getNumberOfAttributes()];
			boolean showWeights = true;
			for (int i = 0; i < getNumberOfSupportVectors(); i++) {
				SupportVector sv = getSupportVector(i);
				if (sv != null) {
					double[] x = sv.getX();
					double alpha = sv.getAlpha();
					double y = sv.getY();
					for (int j = 0; j < w.length; j++) {
						w[j] += y * alpha * x[j];
					}
				} else {
					showWeights = false;
				}
			}
			if (showWeights) {
				for (int j = 0; j < w.length; j++) {
					int nameIndex = weightTable.mapString(0, attributeNames[j]);
					weightTable.add(new SimpleDataTableRow(new double[] { nameIndex, w[j] }));
				}
				return weightTable;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
