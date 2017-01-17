package io.sugo.pio.example.set;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeTransformation;
import io.sugo.pio.example.table.AttributeTypeException;
import io.sugo.pio.example.table.NominalMapping;

/**
 * This transformation returns the remapped value.
 * 
 * @author Ingo Mierswa
 */
public class AttributeTransformationRemapping implements AttributeTransformation {

	private static final long serialVersionUID = 1L;

	private NominalMapping overlayedMapping;

	public AttributeTransformationRemapping(NominalMapping overlayedMapping) {
		this.overlayedMapping = overlayedMapping;
	}

	public AttributeTransformationRemapping(AttributeTransformationRemapping other) {
		this.overlayedMapping = (NominalMapping) other.overlayedMapping.clone();
	}

	@Override
	public Object clone() {
		return new AttributeTransformationRemapping(this);
	}

	public void setNominalMapping(NominalMapping mapping) {
		this.overlayedMapping = mapping;
	}

	@Override
	public double transform(Attribute attribute, double value) {
		if (Double.isNaN(value)) {
			return value;
		}
		if (attribute.isNominal()) {
			try {
				String nominalValue = attribute.getMapping().mapIndex((int) value);
				int index = overlayedMapping.getIndex(nominalValue);
				if (index < 0) {
					return Double.NaN;
					// return value;
				} else {
					return index;
				}
			} catch (AttributeTypeException e) {
				return Double.NaN;
				// throw new AttributeTypeException("Attribute '" + attribute.getName() + "': " +
				// e.getMessage());
			}
		} else {
			return value;
		}
	}

	@Override
	public double inverseTransform(Attribute attribute, double value) {
		if (Double.isNaN(value)) {
			return value;
		}
		if (attribute.isNominal()) {
			try {
				String nominalValue = overlayedMapping.mapIndex((int) value);
				int newValue = attribute.getMapping().getIndex(nominalValue);
				if (newValue < 0) {
					return value;
				} else {
					return newValue;
				}
			} catch (AttributeTypeException e) {
				return value;
				// throw new AttributeTypeException("Attribute '" + attribute.getName() + "': " +
				// e.getMessage());
			}
		} else {
			return value;
		}
	}

	@Override
	public boolean isReversable() {
		return true;
	}
}
