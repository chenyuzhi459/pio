package io.sugo.pio.operator;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ModelViewExampleSet;

/**
 * The view model is typically used for preprocessing models. With help of the
 * {@link ModelViewExampleSet} one can create a new view of the data by applying the necessary
 * transformations defined by this view model.
 * 
 * @author Sebastian Land
 */
public interface ViewModel extends Model {

	/**
	 * This method has to return a legal Attributes object containing every Attribute, the view
	 * should contain
	 * 
	 * @return The attribute object
	 */
	public abstract Attributes getTargetAttributes(ExampleSet viewParent);

	/**
	 * This method has to provide the attribute value mapping for the view. The views attributes
	 * will ask this method for their value.
	 * 
	 * @param targetAttribute
	 *            the attribute, which asks for his value
	 * @param value
	 *            the value the source attribute had in original data
	 * @return the value the attribute should have in target view
	 */
	public abstract double getValue(Attribute targetAttribute, double value);

}
