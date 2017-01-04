package io.sugo.pio.parameter;


import io.sugo.pio.ports.InputPort;

/**
 * A ParameterType for DateFormats.
 *
 * @author Simon Fischer
 *
 */
public class ParameterTypeDateFormat extends ParameterTypeStringCategory {

	private static final long serialVersionUID = 1L;

	private transient InputPort inPort;

	private ParameterTypeAttribute attributeParameter;

	public static final String[] PREDEFINED_DATE_FORMATS = new String[] { "", "yyyy.MM.dd G 'at' HH:mm:ss z",
			"EEE, MMM d, ''yy", "h:mm a", "hh 'o''clock' a, zzzz", "K:mm a, z", "yyyy.MMMMM.dd GGG hh:mm aaa",
			"EEE, d MMM yyyy HH:mm:ss Z", "yyMMddHHmmssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
			"M/d/yy h:mm a" };

	/**
	 * This is the constructor for date format if no example set meta data is available.
	 */
	public ParameterTypeDateFormat(String key, String description) {
		this(null, key, description, null);
	}

	/**
	 * This is the constructor for date format if no example set meta data is available.
	 */
	public ParameterTypeDateFormat(String key, String description, String defaultValue) {
		this(null, key, description, defaultValue, null);
	}

	/**
	 * This is the constructor for parameter types of operators which transform an example set.
	 */
	public ParameterTypeDateFormat(ParameterTypeAttribute attributeParameter, String key, String description,
								   InputPort inPort) {
		this(attributeParameter, key, description, "", inPort);
	}

	/**
	 * This is the constructor for parameter types of operators which transform an example set.
	 */
	public ParameterTypeDateFormat(ParameterTypeAttribute attributeParameter, String key, String description,
								   String defaultValue, InputPort inPort) {
		super(key, description, PREDEFINED_DATE_FORMATS, defaultValue, true);
		this.inPort = inPort;
		this.attributeParameter = attributeParameter;
	}

	public InputPort getInputPort() {
		return inPort;
	}

	/**
	 * This method returns the referenced attribute parameter or null if non exists.
	 */
	public ParameterTypeAttribute getAttributeParameterType() {
		return attributeParameter;
	}

}
