package io.sugo.pio.parameter;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A parameter type for Dates. It is represented by a {@link DatePicker} element in the GUI. The
 * date is internally stored as string. To get a {@link Date} object use
 * {@link ParameterTypeDate#getParameterAsDate(String, Operator)}.
 *
 * @author Nils Woehler
 *
 */
public class ParameterTypeDate extends ParameterTypeSingle {

	private static final long serialVersionUID = 1L;

	private Date defaultValue = null;

	public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT;

	static {
		// ThreadLocale because this is static and used by other threads
		// and DateFormats are NOT threadsafe
		DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {

			SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

			@Override
			protected SimpleDateFormat initialValue() {
				return format;
			}
		};
	}

	public ParameterTypeDate(String key, String description) {
		super(key, description);
	}

	public ParameterTypeDate(String key, String description, Date defaultValue) {
		super(key, description);
		setDefaultValue(defaultValue);
	}

	public ParameterTypeDate(String key, String description, boolean optional, boolean expert) {
		super(key, description);
		setOptional(optional);
		setDefaultValue(null);
	}

	public ParameterTypeDate(String key, String description, Date defaultValue, boolean optional, boolean expert) {
		super(key, description);
		setOptional(optional);
		setDefaultValue(defaultValue);
	}

	@Override
	public String getRange() {
		return null; // no range here as showRange() returns null
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Date) defaultValue;
	}

	@Override
	public boolean isNumerical() {
		return false;
	}

	@Override
	public String toString(Object value) {
		if (value == null) {
			return "";
		}
		String ret = null;
		if (value instanceof Date) {
			ret = DATE_FORMAT.get().format(value);
		} else {
			ret = String.valueOf(value);
		}
		return ret;
	}


	public static Date getParameterAsDate(String key, Operator operator) throws UndefinedParameterError, UserError {
		String value = operator.getParameter(key);
		if (value == null || value.trim().isEmpty()) {
			throw new UndefinedParameterError(key, operator);
		}
		try {
			return ParameterTypeDate.DATE_FORMAT.get().parse(value);
		} catch (ParseException e) {
			throw new UserError(operator, "wrong_date_format", value, key.replace('_', ' '));
		}
	}

	/**
	 * Can be used to check whether the current string is a valid date string for this parameter
	 * type.
	 */
	public static boolean isValidDate(String dateString) {
		try {
			return dateString != null && DATE_FORMAT.get().parse(dateString) != null;
		} catch (ParseException e) {
			return false;
		}
	}

}
