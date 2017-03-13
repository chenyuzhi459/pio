package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.UndefinedParameterError;


/**
 * This precondition wraps around another precondition and performs the check only if a parameter
 * combination is fulfilled. If not, no checks will be performed. This might be used for parameter
 * dependency aware checking.
 * 
 */
public class ParameterConditionedPrecondition extends AbstractPrecondition {

	private final Precondition condition;
	private final String parameterKey;
	private final String parameterValue;
	private final ParameterHandler handler;

	public ParameterConditionedPrecondition(InputPort inputPort, Precondition condition, ParameterHandler handler,
                                            String parameterKey, String parameterValue) {
		super(inputPort);
		this.condition = condition;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
		this.handler = handler;
	}

	@Override
	public void assumeSatisfied() {
		condition.assumeSatisfied();
	}

	@Override
	public void check(MetaData metaData) {
		try {
			if (handler.getParameterAsString(parameterKey).equals(parameterValue)) {
				condition.check(metaData);
			}
		} catch (UndefinedParameterError e) {
			// condition not applicable
		}
	}

	@Override
	public String getDescription() {
		return condition.getDescription();
	}

	@Override
	public boolean isCompatible(MetaData input, CompatibilityLevel level) {
		return condition.isCompatible(input, level);
	}

	@Override
	public MetaData getExpectedMetaData() {
		return condition.getExpectedMetaData();
	}

}
