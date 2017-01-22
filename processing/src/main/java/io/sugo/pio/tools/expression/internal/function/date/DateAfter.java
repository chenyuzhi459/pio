package io.sugo.pio.tools.expression.internal.function.date;

import java.util.Date;


/**
 * A {@link Function} for comparing two dates.
 *
 * @author David Arnu
 *
 */
public class DateAfter extends Abstract2DateInputBooleanOutput {

	public DateAfter() {
		super("date.date_after");
	}

	/**
	 * Compares two dates and returns true if the the second date is after the first
	 */
	@Override
	protected Boolean compute(Date left, Date right) {

		if (left == null || right == null) {
			return null;
		}

		return left.after(right);
	}

}
