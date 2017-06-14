package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeString;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a condition for the AttributeFilter operator. It provides the possibility
 * to check if all values of a numerical attribute match a condition. This conditions might be
 * specified by != or <>, =, <, <=, >, >= followed by a value. For example like this: "> 6.5" would
 * keep all attributes having only values greater 6.5. This single conditions might be combined by
 * || or && but not mixed. Example: "> 6.5 && < 11" would keep all attributes containing only values
 * between 6.5 and 11. Whitespaces (that do not disrupt relational operators, && or ||) will be
 * ignored, so it will make no difference if the condition is for example ">6.5&&<11" or
 * " > 6.5 && < 11 "
 *
 * @author Sebastian Land, Ingo Mierswa, Marcel Seifert
 */
public class NumericValueAttributeFilter extends AbstractAttributeFilterCondition {

	/**
	 * Condition Operators
	 */
	private static final String CONDITION_OPERATOR_NOT_EQUAL = "<>";
	private static final String CONDITION_OPERATOR_NOT_EQUAL_2 = "!=";
	private static final String CONDITION_OPERATOR_LESS_OR_EQUAL = "<=";
	private static final String CONDITION_OPERATOR_LESS = "<";
	private static final String CONDITION_OPERATOR_GREATER_OR_EQUAL = ">=";
	private static final String CONDITION_OPERATOR_GREATER = ">";
	private static final String CONDITION_OPERATOR_EQUAL = "=";

	public static String PARAMETER_NUMERIC_CONDITION = "numeric_condition";

	private static final String[] CONDITION_OPERATORS = { CONDITION_OPERATOR_NOT_EQUAL, CONDITION_OPERATOR_NOT_EQUAL_2,
			CONDITION_OPERATOR_LESS_OR_EQUAL, CONDITION_OPERATOR_LESS, CONDITION_OPERATOR_GREATER_OR_EQUAL,
			CONDITION_OPERATOR_GREATER, CONDITION_OPERATOR_EQUAL };

	private Attribute lastCheckedAttribute = null;

	private ArrayList<Condition> conditions;

	private boolean keep = true;

	private boolean conjunctiveMode;

	private static class Condition {

		private int condition;

		private double value;

		public Condition(String condition, String value) {
			this.value = Double.parseDouble(value);
			if (condition.equals(CONDITION_OPERATOR_NOT_EQUAL) || condition.equals(CONDITION_OPERATOR_NOT_EQUAL_2)) {
				this.condition = 1;
			} else if (condition.equals(CONDITION_OPERATOR_LESS_OR_EQUAL)) {
				this.condition = 2;
			} else if (condition.equals(CONDITION_OPERATOR_LESS)) {
				this.condition = 3;
			} else if (condition.equals(CONDITION_OPERATOR_GREATER_OR_EQUAL)) {
				this.condition = 4;
			} else if (condition.equals(CONDITION_OPERATOR_GREATER)) {
				this.condition = 5;
			} else if (condition.equals(CONDITION_OPERATOR_EQUAL)) {
				this.condition = 0;
			}
		}

		public boolean check(double value) {
			if (Double.isNaN(value)) {
				return true;
			}

			switch (condition) {
				case 0:
					return value == this.value;
				case 1:
					return value != this.value;
				case 2:
					return value <= this.value;
				case 3:
					return value < this.value;
				case 4:
					return value >= this.value;
				case 5:
					return value > this.value;
			}
			return false;
		}
	}

	@Override
	public void init(ParameterHandler parameterHandler) throws UserError, ConditionCreationException {
		String conditionString = parameterHandler.getParameterAsString(PARAMETER_NUMERIC_CONDITION);
		Operator operator = null;
		if (parameterHandler instanceof Operator) {
			operator = (Operator) parameterHandler;
		}

		if (conditionString == null || conditionString.length() == 0) {
			throw new UserError(operator, "cannot_parse_expression", StringEscapeUtils.escapeHtml(conditionString));
		}
		// testing if not allowed combination of and and or
		if (conditionString.contains("||") && conditionString.contains("&&")) {
			throw new UserError(operator, "cannot_parse_expression", StringEscapeUtils.escapeHtml(conditionString));
		}

		this.conjunctiveMode = conditionString.contains("&&");

		conditions = new ArrayList<>();
		boolean conditionFound;
		for (String conditionSubString : conditionString.split("[|&]{2}")) {

			conditionFound = false;
			for (String conditionOperator : CONDITION_OPERATORS) {
				if (conditionSubString.trim().startsWith(conditionOperator)) {
					conditionFound = true;
					// Quotation needed, to prevent conditionOperator to be interpreted as a regex
					String number = conditionSubString.replaceFirst("\\Q" + conditionOperator + "\\E", "").trim();

					// check if number is valid
					try {
						Double.parseDouble(number);
						conditions.add(new Condition(conditionOperator, number));
						break;
					} catch (NullPointerException | NumberFormatException e) {
						throw new UserError(operator, "cannot_parse_expression",
								StringEscapeUtils.escapeHtml(conditionString));
					}

				}
			}
			if (!conditionFound) {
				throw new UserError(operator, "cannot_parse_expression", StringEscapeUtils.escapeHtml(conditionString));
			}
		}
	}

	@Override
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler handler) {
		// TODO: If some infos over the value range are available: Use them to decide if possible
		return MetaDataInfo.UNKNOWN;
	}

	@Override
	public boolean isNeedingScan() {
		return true;
	}

	/**
	 * Don't remove any attribute without checking values
	 */
	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		return ScanResult.UNCHECKED;
	}

	@Override
	public ScanResult check(Attribute attribute, Example example) {
		if (lastCheckedAttribute != attribute) {
			keep = true;
		}
		if (attribute.isNumerical()) {
			boolean exampleResult;
			double checkValue = example.getValue(attribute);

			if (conjunctiveMode) {
				exampleResult = true;
				for (Condition condition : conditions) {
					exampleResult &= condition.check(checkValue);
				}
			} else {
				exampleResult = false;
				for (Condition condition : conditions) {
					exampleResult |= condition.check(checkValue);
				}
			}
			keep &= exampleResult;
		}
		if (!keep && attribute.isNumerical()) {
			return ScanResult.REMOVE;
		} else {
			return ScanResult.UNCHECKED;
		}
	}

	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, Port inPort, int... valueTypes) {
		LinkedList<ParameterType> types = new LinkedList<ParameterType>();
		types.add(new ParameterTypeString(PARAMETER_NUMERIC_CONDITION,
				I18N.getMessage("pio.NumericValueAttributeFilter.numeric_condition"),
				true));
		return types;
	}

}
