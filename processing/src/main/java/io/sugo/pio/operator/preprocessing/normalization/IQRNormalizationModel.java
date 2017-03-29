package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.*;
import io.sugo.pio.example.table.ViewAttribute;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.container.Tupel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This is the normalization model for the IQR normalization method.
 * 
 * @author Brendon Bolin, Sebastian Land
 */
public class IQRNormalizationModel extends AbstractNormalizationModel {

	private static final long serialVersionUID = 4333931155320624490L;

	private HashMap<String, Tupel<Double, Double>> attributeMeanSigmaMap;

	public IQRNormalizationModel(ExampleSet exampleSet, HashMap<String, Tupel<Double, Double>> attributeMeanSigmaMap) {
		super(exampleSet);
		this.attributeMeanSigmaMap = attributeMeanSigmaMap;
	}

	/**
	 * Returns a nicer name. Necessary since this model is defined as inner class.
	 */
	@Override
	public String getName() {
		return "IQR-Transformation";
	}

	@Override
	public Attributes getTargetAttributes(ExampleSet viewParent) {
		SimpleAttributes attributes = new SimpleAttributes();
		// add special attributes to new attributes
		Iterator<AttributeRole> roleIterator = viewParent.getAttributes().allAttributeRoles();
		while (roleIterator.hasNext()) {
			AttributeRole role = roleIterator.next();
			if (role.isSpecial()) {
				attributes.add(role);
			}
		}
		// add regular attributes
		for (Attribute attribute : viewParent.getAttributes()) {
			if (!attribute.isNumerical() || !attributeMeanSigmaMap.containsKey(attribute.getName())) {
				attributes.addRegular(attribute);
			} else {
				// giving new attributes old name: connection to rangesMap
				attributes.addRegular(new ViewAttribute(this, attribute, attribute.getName(), Ontology.NUMERICAL, null));
			}
		}
		return attributes;
	}

	@Override
	public double getValue(Attribute targetAttribute, double value) {
		Tupel<Double, Double> meanSigmaTupel = attributeMeanSigmaMap.get(targetAttribute.getName());
		if (meanSigmaTupel != null) {
			if (meanSigmaTupel.getSecond().doubleValue() <= 0) {
				return (0);
			} else {
				return ((value - meanSigmaTupel.getFirst().doubleValue()) / meanSigmaTupel.getSecond().doubleValue());
			}
		}
		return value;
	}
	
	public Map<String, Tupel<Double, Double>> getAttributeMeanSigmaMap() {
		return attributeMeanSigmaMap;
	}
}
