package io.sugo.pio.ports.metadata;


import io.sugo.pio.operator.Model;
import io.sugo.pio.ports.InputPort;

/**
 * This class is holds the informations for all models. It is the parent class of each more model
 * specific implementation, which should be able to simulate all changes on the data of a model
 * application during meta data transformation. This super class remembers the meta data of the
 * trainings set and already checks compatibility of the application meta data with the trainings
 * meta data. This is done in an equal way to the PredictionModel.
 * 
 * TODO: This model needs to become abstract in order to force all operators to implement a proper
 * and problem specific ModelMetaData object.
 * 
 * @author Simon Fischer, Sebastian Land
 */
public class ModelMetaData extends MetaData {

	private static final long serialVersionUID = 1L;

	private ExampleSetMetaData trainingSetMetaData;

	public ModelMetaData(ExampleSetMetaData trainingSetMetaData) {
		this(Model.class, trainingSetMetaData);
	}

	public ModelMetaData(Class<? extends Model> mclass, ExampleSetMetaData trainingSetMetaData) {
		super(mclass);
		this.trainingSetMetaData = trainingSetMetaData;
	}

	/**
	 * This method simulates the application of a model. First the compatibility of the model with
	 * the current example set is checked and then the effects are applied.
	 */
	public final ExampleSetMetaData apply(ExampleSetMetaData emd, InputPort inputPort) {
		checkCompatibility(emd, inputPort);
		return applyEffects(emd, inputPort);
	}

	private void checkCompatibility(ExampleSetMetaData emd, InputPort inputPort) {

	}

	/**
	 * This method must be implemented by subclasses in order to apply any changes on the meta data,
	 * that would occur on application of the real model. TODO: This method should be abstract.
	 */
	protected ExampleSetMetaData applyEffects(ExampleSetMetaData emd, InputPort inputPort) {
		return emd;
	}

	public ExampleSetMetaData getTrainingSetMetaData() {
		return trainingSetMetaData;
	}

}