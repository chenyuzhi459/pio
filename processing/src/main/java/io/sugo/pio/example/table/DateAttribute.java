package io.sugo.pio.example.table;

import io.sugo.pio.example.AttributeTransformation;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;

import java.util.Date;


/**
 * This class holds all information on a single date attribute. In addition to the information of
 * the superclass, this is some statistics data like minimum, maximum and average of the values.
 * 
 * @author Ingo Mierswa
 */
public class DateAttribute extends AbstractAttribute {

	private static final long serialVersionUID = -685655991653799960L;

	/**
	 * Creates a simple attribute which is not part of a series and does not provide a unit string.
	 */
	protected DateAttribute(String name) {
		this(name, Ontology.DATE);
	}

	/**
	 * Creates a simple attribute which is not part of a series and does not provide a unit string.
	 */
	protected DateAttribute(String name, int valueType) {
		super(name, valueType);
	}

	/**
	 * Clone constructor.
	 */
	private DateAttribute(DateAttribute a) {
		super(a);
	}

	@Override
	public Object clone() {
		return new DateAttribute(this);
	}

	@Override
	public AttributeTransformation getLastTransformation() {
		return null;
	}

	@Override
	public String getAsString(double value, int digits, boolean quoteNominal) {
		if (Double.isNaN(value)) {
			return "?";
		} else {
			long milliseconds = (long) value;
			String result = null;
			if (getValueType() == Ontology.DATE) {
				result = Tools.formatDate(new Date(milliseconds));
			} else if (getValueType() == Ontology.TIME) {
				result = Tools.formatTime(new Date(milliseconds));
			} else if (getValueType() == Ontology.DATE_TIME) {
				result = Tools.formatDateTime(new Date(milliseconds), "dd/MM/yyyy HH:mm:ss aa zzz");
			}
			if (quoteNominal) {
				result = "\"" + result + "\"";
			}
			return result;
		}
	}

	/** Returns null. */
	@Override
	public NominalMapping getMapping() {
		throw new UnsupportedOperationException(
				"The method getNominalMapping() is not supported by date attributes! You probably tried to execute an operator on a date or time data which is only able to handle nominal values. You could use one of the Date to Nominal operator before this application.");
	}

	/** Returns false. */
	@Override
	public boolean isNominal() {
		return false;
	}

	@Override
	public boolean isNumerical() {
		return false;
	}

	/** Do nothing. */
	@Override
	public void setMapping(NominalMapping nominalMapping) {}

	@Override
	public void setBlockType(int b) {

	}

	@Override
	public boolean isDateTime() {
		return true;
	}
}
