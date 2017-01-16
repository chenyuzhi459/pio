package io.sugo.pio.ports.metadata;


import io.sugo.pio.example.Attributes;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Range;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Simon Fischer
 * 
 */
public class PredictionModelMetaData extends ModelMetaData {

	private static final long serialVersionUID = 1L;

	private AttributeMetaData predictedLabelMetaData;

	private List<AttributeMetaData> generatedPredictionAttributes = new LinkedList<AttributeMetaData>();

	/** Clone constructor */
	protected PredictionModelMetaData() {}

	public PredictionModelMetaData(Class<? extends PredictionModel> modelClass) {
		this(modelClass, null);
	}

	public PredictionModelMetaData(Class<? extends PredictionModel> modelClass, ExampleSetMetaData trainingSetMetaData) {
		super(modelClass, trainingSetMetaData);
		if (trainingSetMetaData != null) {
			AttributeMetaData labelAttributeMetaData = trainingSetMetaData.getLabelMetaData();
			if (labelAttributeMetaData != null) {
				this.predictedLabelMetaData = labelAttributeMetaData.copy();
				this.predictedLabelMetaData.setRole(Attributes.PREDICTION_NAME);
				this.predictedLabelMetaData.setName("prediction(" + predictedLabelMetaData.getName() + ")");
				if (predictedLabelMetaData.isNumerical()) {
					this.predictedLabelMetaData.setValueSetRelation(SetRelation.SUPERSET);
				}
				this.predictedLabelMetaData.setMean(new MDReal());

				// creating confidence attributes
				generatedPredictionAttributes.add(predictedLabelMetaData);
				if (predictedLabelMetaData.isNominal()) {
					if (predictedLabelMetaData.getValueSet().isEmpty()) {
						AttributeMetaData confidence = new AttributeMetaData(Attributes.CONFIDENCE_NAME + "(?)",
								Ontology.REAL, Attributes.CONFIDENCE_NAME + "_" + "?");
						confidence.setValueRange(new Range(0, 1), SetRelation.SUBSET);
						generatedPredictionAttributes.add(confidence);
						predictedLabelMetaData.setValueSetRelation(SetRelation.SUPERSET);
					} else {
						for (String value : predictedLabelMetaData.getValueSet()) {
							AttributeMetaData confidence = new AttributeMetaData(Attributes.CONFIDENCE_NAME + "(" + value
									+ ")", Ontology.REAL, Attributes.CONFIDENCE_NAME + "_" + value);
							confidence.setValueRange(new Range(0, 1), SetRelation.SUBSET);
							generatedPredictionAttributes.add(confidence);
						}
					}
				}
			}
		}
	}

	@Override
	public ExampleSetMetaData applyEffects(ExampleSetMetaData emd, InputPort inputPort) {
		if (predictedLabelMetaData == null) {
			return emd;
		}
		List<AttributeMetaData> predictionAttributes = getPredictionAttributeMetaData();
		if (predictionAttributes != null) {
			emd.addAllAttributes(predictionAttributes);
			emd.mergeSetRelation(getPredictionAttributeSetRelation());
		}
		return emd;
	}

	public List<AttributeMetaData> getPredictionAttributeMetaData() {
		return generatedPredictionAttributes;
	}

	public AttributeMetaData getPredictedLabelMetaData() {
		return predictedLabelMetaData;
	}

	public SetRelation getPredictionAttributeSetRelation() {
		if (predictedLabelMetaData != null) {
			return predictedLabelMetaData.getValueSetRelation();
		} else {
			return SetRelation.UNKNOWN;
		}
	}

	@Override
	public String getDescription() {
		return super.getDescription() + "; generates: " + predictedLabelMetaData;
	}

	@Override
	public PredictionModelMetaData clone() {
		PredictionModelMetaData clone = (PredictionModelMetaData) super.clone();
		if (this.predictedLabelMetaData != null) {
			clone.predictedLabelMetaData = this.predictedLabelMetaData.clone();
		}
		if (this.generatedPredictionAttributes != null) {
			clone.generatedPredictionAttributes = new LinkedList<>();
			for (AttributeMetaData amd : this.generatedPredictionAttributes) {
				clone.generatedPredictionAttributes.add(amd.clone());
			}
		}
		return clone;
	}

}
