package io.sugo.pio.tools.expression.internal.function.date;


import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.FunctionInputException;

import java.util.Date;


/**
 * A {@link Function} for calculating the difference between two dates in milliseconds.
 *
 * @author David Arnu
 */
public class DateDiff extends Abstract2DateInputIntegerOutputFunction {

    public DateDiff() {
        super("date.date_diff", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS);
    }

    @Override
    protected double compute(Date left, Date right, String valueLocale, String valueTimezone) {
        if (left == null || right == null) {
            return Double.NaN;
        } else {

            return right.getTime() - left.getTime();
        }
    }

    @Override
    protected ExpressionType computeType(ExpressionType... inputTypes) {

        // locale and time zone arguments just accepted out of compatibility reasons, but are no
        // longer documented
        if (inputTypes.length != 2 && inputTypes.length != 4) {
            throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), "2",
                    inputTypes.length);
        }
        ExpressionType firstType = inputTypes[0];
        ExpressionType secondType = inputTypes[1];
        if (firstType != ExpressionType.DATE || secondType != ExpressionType.DATE) {
            throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "date");
        }
        if (inputTypes.length == 4) {
            if (inputTypes[2] != ExpressionType.STRING) {
                throw new FunctionInputException("expression_parser.function_date_diff_depricated", getFunctionName());
            }
            if (inputTypes[3] != ExpressionType.STRING) {
                throw new FunctionInputException("expression_parser.function_date_diff_depricated", getFunctionName());
            }
        }
        return ExpressionType.INTEGER;

    }

}
