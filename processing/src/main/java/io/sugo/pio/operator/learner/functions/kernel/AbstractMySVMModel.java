package io.sugo.pio.operator.learner.functions.kernel;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorProgress;
import io.sugo.pio.operator.learner.FormulaProvider;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExample;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples.MeanVariance;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.KernelDot;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.tools.Tools;

import java.util.Iterator;
import java.util.Map;


/**
 * The abstract superclass for the SVM models by Stefan Rueping.
 *
 */
public abstract class AbstractMySVMModel extends KernelModel implements FormulaProvider {

	private static final long serialVersionUID = 2812901947459843681L;

	private static final int OPERATOR_PROGRESS_STEPS = 5000;

	private io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples model;

	private Kernel kernel;

	private double[] weights = null;

	public AbstractMySVMModel(ExampleSet exampleSet,
                              io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples model, Kernel kernel,
                              int kernelType) {
		super(exampleSet, ExampleSetUtilities.SetsCompareOption.ALLOW_SUPERSET,
				ExampleSetUtilities.TypesCompareOption.ALLOW_SAME_PARENTS);
		this.model = model;
		this.kernel = kernel;

		if (this.kernel instanceof KernelDot) {
			this.weights = new double[getNumberOfAttributes()];
			for (int i = 0; i < getNumberOfSupportVectors(); i++) {
				SupportVector sv = getSupportVector(i);
				if (sv != null) {
					double[] x = sv.getX();
					double alpha = sv.getAlpha();
					double y = sv.getY();
					for (int j = 0; j < weights.length; j++) {
						weights[j] += y * alpha * x[j];
					}
				} else {
					this.weights = null;
					break;
				}
			}
		}
	}

	/** Creates a new SVM for prediction. */
	public abstract SVMInterface createSVM();

	@Override
	public boolean isClassificationModel() {
		return getLabel().isNominal();
	}

	@Override
	public double getBias() {
		return model.get_b();
	}

	/**
	 * This method must divide the alpha by the label since internally the alpha value is already
	 * multiplied with y.
	 */
	@Override
	public SupportVector getSupportVector(int index) {
		double alpha = model.get_alpha(index);
		double y = model.get_y(index);
		if (y != 0.0d) {
			alpha /= y;
		}
		return new SupportVector(model.get_example(index).toDense(getNumberOfAttributes()), y, alpha);
	}

	@Override
	public double getAlpha(int index) {
		return model.get_alpha(index);
	}

	@Override
	public String getId(int index) {
		return model.getId(index);
	}

	@Override
	public int getNumberOfSupportVectors() {
		return model.count_examples();
	}

	@Override
	public int getNumberOfAttributes() {
		return model.get_dim();
	}

	@Override
	public double getAttributeValue(int exampleIndex, int attributeIndex) {
		io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExample sVMExample = model
				.get_example(exampleIndex);
		double value = 0.0d;
		try {
			value = sVMExample.toDense(getNumberOfAttributes())[attributeIndex];
		} catch (ArrayIndexOutOfBoundsException e) {
			// dense array to short --> use default value
		}
		return value;
	}

	@Override
	public String getClassificationLabel(int index) {
		double y = model.get_y(index);
		if (y < 0) {
			return getLabel().getMapping().getNegativeString();
		} else {
			return getLabel().getMapping().getPositiveString();
		}
	}

	@Override
	public double getRegressionLabel(int index) {
		return model.get_y(index);
	}

	@Override
	public double getFunctionValue(int index) {
		SVMInterface svm = createSVM();
		svm.init(kernel, model);
		// need to clone the support vector, since internally there is only one instance of an
		// SVMExample
		// in the data, where only its data pointers are exchanged. This instance is also changed in
		// svm.predict(),
		// so we need to clone.
		SVMExample sv = new SVMExample(model.get_example(index));
		return svm.predict(sv);
	}

	/** Gets the kernel. */
	public Kernel getKernel() {
		return kernel;
	}

	/** Gets the model, i.e. an SVM example set. */
	public io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples getExampleSet() {
		return model;
	}

	/**
	 * Sets the correct prediction to the example from the result value of the SVM.
	 */
	public abstract void setPrediction(Example example, double prediction);

	@Override
	public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabelAttribute) throws OperatorException {
		if (kernel instanceof KernelDot) {
			if (weights != null) {
				Map<Integer, MeanVariance> meanVariances = model.getMeanVariances();
				OperatorProgress progress = null;
				if (getShowProgress() && getOperator() != null && getOperator().getProgress() != null) {
					progress = getOperator().getProgress();
					progress.setTotal(exampleSet.size());
				}
				int progressCounter = 0;
				for (Example example : exampleSet) {
					double prediction = getBias();
					int a = 0;
					for (Attribute attribute : exampleSet.getAttributes()) {
						double value = example.getValue(attribute);
						MeanVariance meanVariance = meanVariances.get(a);
						if (meanVariance != null) {
							if (meanVariance.getVariance() == 0.0d) {
								value = 0.0d;
							} else {
								value = (value - meanVariance.getMean()) / Math.sqrt(meanVariance.getVariance());
							}
						}
						prediction += weights[a] * value;
						a++;
					}
					setPrediction(example, prediction);

					if (progress != null && ++progressCounter % OPERATOR_PROGRESS_STEPS == 0) {
						progress.setCompleted(progressCounter);
					}
				}
				return exampleSet;
			}
		}

		// only if not simple dot hyperplane (see above)...
		io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples toPredict =
				new io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples(
				exampleSet, exampleSet.getAttributes().getPredictedLabel(), model.getMeanVariances());

		SVMInterface svm = createSVM();
		svm.init(kernel, model);
		svm.predict(toPredict);

		// set predictions from toPredict
		Iterator<Example> reader = exampleSet.iterator();
		int k = 0;
		while (reader.hasNext()) {
			setPrediction(reader.next(), toPredict.get_y(k++));
		}
		return exampleSet;
	}

	@Override
	public String getFormula() {
		StringBuffer result = new StringBuffer();

		Kernel kernel = getKernel();

		boolean first = true;
		for (int i = 0; i < getNumberOfSupportVectors(); i++) {
			SupportVector sv = getSupportVector(i);
			if (sv != null) {
				double alpha = sv.getAlpha();
				if (!Tools.isZero(alpha)) {
					result.append(Tools.getLineSeparator());
					double[] x = sv.getX();
					double y = sv.getY();
					double factor = y * alpha;
					if (factor < 0.0d) {
						if (first) {
							result.append("- " + Math.abs(factor));
						} else {
							result.append("- " + Math.abs(factor));
						}
					} else {
						if (first) {
							result.append("  " + factor);
						} else {
							result.append("+ " + factor);
						}
					}

					result.append(" * (" + kernel.getDistanceFormula(x, getAttributeConstructions()) + ")");
					first = false;
				}
			}
		}

		double bias = getBias();
		if (!Tools.isZero(bias)) {
			result.append(Tools.getLineSeparator());
			if (bias < 0.0d) {
				if (first) {
					result.append("- " + Math.abs(bias));
				} else {
					result.append("- " + Math.abs(bias));
				}
			} else {
				if (first) {
					result.append(bias);
				} else {
					result.append("+ " + bias);
				}
			}
		}

		return result.toString();
	}

	/**
	 * Need to synchronized since {@link SVMExamples#get_example(int)} is not thread safe. (That
	 * method always returns the same instance.)
	 */
	@Override
	public synchronized String toString() {
		return super.toString();
	}
}
