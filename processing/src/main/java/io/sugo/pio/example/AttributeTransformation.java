package io.sugo.pio.example;

import java.io.Serializable;


/**
 * This interface is used to define on-the-fly transformations in data views.
 * 
 * @author Ingo Mierswa
 */
public interface AttributeTransformation extends Serializable {

	public Object clone();

	public double transform(Attribute attribute, double value);

	public double inverseTransform(Attribute attribute, double value);

	public boolean isReversable();

}
