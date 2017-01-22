package io.sugo.pio.tools.expression.internal.function.date;

import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionEvaluator;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.Calendar;


/**
 * A {@link Function} that returns the current date.
 *
 * @author David Arnu
 */
public class DateNow extends AbstractFunction {

    public DateNow() {
        super("date.date_now", 0, Ontology.DATE_TIME);
    }

    @Override
    public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
        if (inputEvaluators.length > 0) {
            throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 0,
                    inputEvaluators.length);
        }
        ExpressionType resultType = getResultType(inputEvaluators);
        return new SimpleExpressionEvaluator(Calendar.getInstance().getTime(), resultType);
    }

    @Override
    protected ExpressionType computeType(ExpressionType... inputTypes) {

        return ExpressionType.DATE;
    }

    @Override
    protected boolean isConstantOnConstantInput() {
        return false;
    }
}
