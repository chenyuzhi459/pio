package io.sugo.pio.operator.learner.meta;

import io.sugo.pio.operator.Model;

import java.util.List;


/**
 * This interface provides methods for accessing the different models encapsulated in this model for
 * graphical representation. Models implementing this interface might be rendered using the the
 * MetaModelRenderer.
 * 
 */
public interface MetaModel {

	public List<? extends Model> getModels();

	public List<String> getModelNames();

}
