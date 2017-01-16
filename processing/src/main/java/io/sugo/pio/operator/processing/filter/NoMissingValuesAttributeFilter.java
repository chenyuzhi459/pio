package io.sugo.pio.operator.processing.filter;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;

public class NoMissingValuesAttributeFilter extends AbstractAttributeFilterCondition {

	@Override
	public boolean isNeedingScan() {
		return true;
	}

	@Override
	public ScanResult beforeScanCheck(Attribute attribute) {
		return ScanResult.UNCHECKED;
	}

	@Override
	public ScanResult check(Attribute attribute, Example example) {
		if (Double.isNaN(example.getValue(attribute))) {
			return ScanResult.REMOVE;
		} else {
			return ScanResult.UNCHECKED;
		}
	}
}
