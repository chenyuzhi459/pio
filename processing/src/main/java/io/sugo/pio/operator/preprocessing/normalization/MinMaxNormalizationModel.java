package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.*;
import io.sugo.pio.example.table.ViewAttribute;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.container.Tupel;

import java.util.*;


/**
 * A simple model which can be used to transform all regular attributes into a value range between
 * the given min and max values.
 * 
 * @author Ingo Mierswa, Sebastian Land
 */
public class MinMaxNormalizationModel extends AbstractNormalizationModel {

	private static final long serialVersionUID = 5620317015578777169L;

	/** The minimum value for each attribute after normalization. */
	private final double min;

	/** The maximum value for each attribute after normalization. */
	private final double max;
	private final HashMap<String, Tupel<Double, Double>> attributeRanges;
	private final Set<String> attributeNames;

	/** Create a new normalization model. */
	public MinMaxNormalizationModel(ExampleSet exampleSet, double min, double max,
			HashMap<String, Tupel<Double, Double>> attributeRanges) {
		super(exampleSet);
		this.min = min;
		this.max = max;
		this.attributeRanges = attributeRanges;
		attributeNames = new HashSet<String>();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) {
				attributeNames.add(attribute.getName());
			}
		}
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
			if (!attribute.isNumerical() || !attributeNames.contains(attribute.getName())) {
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
		Tupel<Double, Double> ranges = attributeRanges.get(targetAttribute.getName());
		double minA = ranges.getFirst().doubleValue();
		double maxA = ranges.getSecond().doubleValue();
		if (maxA == minA || min == max) {
			return Math.min(Math.max(minA, min), max);
		} else {
			return (value - minA) / (maxA - minA) * (max - min) + min;
		}
	}

	/**
	 * Returns a nicer name. Necessary since this model is defined as inner class.
	 */
	@Override
	public String getName() {
		return "MinMaxNormalizationModel";
	}

	/** Returns a string representation of this model. */
	@Override
	public String toString() {
		return "Normalize between " + this.min + " and " + this.max;
	}
	
	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public Map<String, Tupel<Double, Double>> getAttributeRanges() {
		return attributeRanges;
	}

	public Set<String> getAttributeNames() {
		return attributeNames;
	}
}
