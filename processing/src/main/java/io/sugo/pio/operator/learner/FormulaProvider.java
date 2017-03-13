package io.sugo.pio.operator.learner;

import io.sugo.pio.operator.Model;


/**
 * This interface indicates that the model is able to produce a human and machine readable formula
 * which can then be parsed by other programs and used for predictions.
 * 
 */
public interface FormulaProvider extends Model {

	public String getFormula();

}
