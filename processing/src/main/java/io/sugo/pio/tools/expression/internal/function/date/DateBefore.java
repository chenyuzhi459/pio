package io.sugo.pio.tools.expression.internal.function.date;

import java.util.Date;


/**
 * A {@link Function} for comparing two dates.
 *
 * @author David Arnu
 *
 */
public class DateBefore extends Abstract2DateInputBooleanOutput {

	public DateBefore() {
		super("date.date_before");
	}

	/**
	 * Compares two dates and returns true if the the second date is before the first
	 */
	@Override
	protected Boolean compute(Date left, Date right) {

		if (left == null || right == null) {
			return null;
		}

		return left.before(right);
	}

}
