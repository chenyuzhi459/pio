package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;

import java.util.LinkedList;
import java.util.List;


public abstract class AbstractAttributeFilterCondition implements AttributeFilterCondition {

	/**
	 * All implementing filter conditions have to have an empty constructor.
	 */
	public AbstractAttributeFilterCondition() {};

	@Override
	public ScanResult check(Attribute attribute, Example example) {
		return ScanResult.UNCHECKED;
	}

	@Override
	public ScanResult checkAfterFullScan() {
		return ScanResult.KEEP;
	}

	@Override
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException {}

	@Override
	public boolean isNeedingScan() {
		return false;
	}

	@Override
	public boolean isNeedingFullScan() {
		return false;
	}

	/**
	 * Just returns an empty list. Subclasses might add parameters
	 */
	@Override
	public List<ParameterType> getParameterTypes(ParameterHandler operator, Port port, int... valueTypes) {
		return new LinkedList<ParameterType>();
	}
}
