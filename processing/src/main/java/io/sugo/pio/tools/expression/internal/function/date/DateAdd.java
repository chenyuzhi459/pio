package io.sugo.pio.tools.expression.internal.function.date;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.ExpressionParserConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A {@link Function} for setting a value of a given date.
 *
 * @author David Arnu
 *
 */
public class DateAdd extends AbstractDateManipulationFunction {

	public DateAdd() {
		super("date.date_add", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.DATE_TIME);
	}

	@Override
	protected Date compute(Date date, double value, String unit, String valueLocale, String valueTimezone) {

		Locale locale;
		TimeZone zone;
		if (valueLocale == null) {
			locale = Locale.getDefault();
		} else {
			locale = new Locale(valueLocale);
		}
		if (valueTimezone == null) {
			zone = TimeZone.getDefault();
		} else {
			zone = TimeZone.getTimeZone(valueTimezone);
		}

		// for missing values as arguments, a missing value is returned
		if (date == null || unit == null || Double.isNaN(value)) {
			return null;
		}

		Calendar cal = Calendar.getInstance(zone, locale);
		cal.setTime(date);

		switch (unit) {
			case ExpressionParserConstants.DATE_UNIT_YEAR:
				cal.add(Calendar.YEAR, (int) value);
				break;
			case ExpressionParserConstants.DATE_UNIT_MONTH:
				cal.add(Calendar.MONTH, (int) value);
				break;
			case ExpressionParserConstants.DATE_UNIT_WEEK:
				cal.add(Calendar.WEEK_OF_YEAR, (int) value);
				break;

			case ExpressionParserConstants.DATE_UNIT_DAY:
				cal.add(Calendar.DAY_OF_MONTH, (int) value);
				break;

			case ExpressionParserConstants.DATE_UNIT_HOUR:
				cal.add(Calendar.HOUR_OF_DAY, (int) value);
				break;
			case ExpressionParserConstants.DATE_UNIT_MINUTE:
				cal.add(Calendar.MINUTE, (int) value);
				break;
			case ExpressionParserConstants.DATE_UNIT_SECOND:
				cal.add(Calendar.SECOND, (int) value);
				break;
			case ExpressionParserConstants.DATE_UNIT_MILLISECOND:
				cal.add(Calendar.MILLISECOND, (int) value);
				break;
			default:
				throw new FunctionInputException("expression_parser.function_wrong_type_at", getFunctionName(),
						"unit constant", "third");

		}
		return cal.getTime();
	}
}
