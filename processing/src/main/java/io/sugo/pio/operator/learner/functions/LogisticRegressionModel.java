package io.sugo.pio.operator.learner.functions;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.ExampleTools;
import io.sugo.pio.operator.learner.SimpleBinaryPredictionModel;
import io.sugo.pio.tools.Tools;

/**
 * The model determined by the {@link LogisticRegression} operator.
 *
 * @author Ingo Mierswa, Tobias Malbrecht
 */
public class LogisticRegressionModel extends SimpleBinaryPredictionModel {

	private static final long serialVersionUID = -966943348790852574L;

	private double[] beta = null;

	private double[] standardError = null;

	private double[] waldStatistic = null;

	private String[] attributeNames;

	private boolean interceptAdded;

	public LogisticRegressionModel(ExampleSet exampleSet, double[] beta, double[] variance, boolean interceptAdded) {
		super(exampleSet, 0.5d);
		this.attributeNames = ExampleTools.getRegularAttributeNames(exampleSet);
		this.beta = beta;
		this.interceptAdded = interceptAdded;

		standardError = new double[variance.length];
		waldStatistic = new double[variance.length];
		for (int j = 0; j < beta.length; j++) {
			standardError[j] = Math.sqrt(variance[j]);
			waldStatistic[j] = beta[j] * beta[j] / variance[j];
		}
	}

	@Override
	public double predict(Example example) {
		double eta = 0.0d;
		int i = 0;
		for (Attribute attribute : example.getAttributes()) {
			double value = example.getValue(attribute);
			eta += beta[i] * value;
			i++;
		}
		if (interceptAdded) {
			eta += beta[beta.length - 1];
		}
		return Math.exp(eta) / (1 + Math.exp(eta));
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (interceptAdded) {
			result.append("Bias (offset): " + Tools.formatNumber(beta[beta.length - 1]));
			result.append("  \t(SE: " + Tools.formatNumber(standardError[standardError.length - 1]));
			result.append(", Wald: " + Tools.formatNumber(waldStatistic[waldStatistic.length - 1]) + ")"
					+ Tools.getLineSeparators(2));
		}
		result.append("Coefficients:" + Tools.getLineSeparator());
		for (int j = 0; j < beta.length - 1; j++) {
			result.append("beta(" + attributeNames[j] + ") = " + Tools.formatNumber(beta[j]));
			result.append(" \t\t(SE: " + Tools.formatNumber(standardError[j]));
			result.append(", Wald: " + Tools.formatNumber(waldStatistic[j]) + ")" + Tools.getLineSeparator());
		}
		result.append(Tools.getLineSeparator() + "Odds Ratios:" + Tools.getLineSeparator());
		for (int j = 0; j < beta.length - 1; j++) {
			result.append("odds_ratio(" + attributeNames[j] + ") = " + Tools.formatNumber(Math.exp(beta[j]))
					+ Tools.getLineSeparator());
		}
		return result.toString();
	}

	public String[] getAttributeNames() {
		return attributeNames;
	}

	public double[] getCoefficients() {
		return beta;
	}

	public String getFirstLabel() {
		return getTrainingHeader().getAttributes().getLabel().getMapping().getNegativeString();
	}

	public String getSecondLabel() {
		return getTrainingHeader().getAttributes().getLabel().getMapping().getPositiveString();
	}
}
