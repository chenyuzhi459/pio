package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.example.*;
import io.sugo.pio.example.table.ViewAttribute;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;
import io.sugo.pio.tools.container.Tupel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This model performs a z-Transformation on the given example set.
 * 
 * @author Ingo Mierswa, Sebastian Land
 */
public class ZTransformationModel extends AbstractNormalizationModel {

	private static final long serialVersionUID = 7739929307307501706L;

	private HashMap<String, Tupel<Double, Double>> attributeMeanVarianceMap;

	public ZTransformationModel(ExampleSet exampleSet, HashMap<String, Tupel<Double, Double>> attributeMeanVarianceMap) {
		super(exampleSet);
		this.attributeMeanVarianceMap = attributeMeanVarianceMap;
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
			if (!attribute.isNumerical() || !attributeMeanVarianceMap.containsKey(attribute.getName())) {
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
		Tupel<Double, Double> meanVarianceTupel = attributeMeanVarianceMap.get(targetAttribute.getName());
		if (meanVarianceTupel != null) {
			if (meanVarianceTupel.getSecond().doubleValue() <= 0) {
				return (0);
			} else {
				return (value - meanVarianceTupel.getFirst().doubleValue())
						/ (Math.sqrt(meanVarianceTupel.getSecond().doubleValue()));
			}
		}
		return value;
	}

	@Override
	public String getName() {
		return "Z-Transformation";
	}

	/** Returns a string representation of this model. */
	@Override
	public String toResultString() {
		StringBuffer result = new StringBuffer();
		result.append("Normalize " + attributeMeanVarianceMap.size() + " attributes to mean 0 and variance 1."
				+ Tools.getLineSeparator() + "Using");
		int counter = 0;
		for (String name : attributeMeanVarianceMap.keySet()) {
			if (counter > 4) {
				result.append(Tools.getLineSeparator() + "... " + (attributeMeanVarianceMap.size() - 5)
						+ " more attributes ...");
				break;
			}
			Tupel<Double, Double> meanVariance = attributeMeanVarianceMap.get(name);
			result.append(Tools.getLineSeparator() + name + " --> mean: " + meanVariance.getFirst().doubleValue()
					+ ", variance: " + meanVariance.getSecond().doubleValue());
			counter++;
		}
		return result.toString();
	}
	
	public Map<String, Tupel<Double, Double>> getAttributeMeanVarianceMap() {
		return attributeMeanVarianceMap;
	}
}
