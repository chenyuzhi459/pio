package io.sugo.pio.operator.processing.filter;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.operator.UserError;

/**
 * This filter actually does nothing and removes no attribute. It is needed to create a behavior
 * consistent with earlier versions and to allow to bundle the filtering with an
 * AttributeSubsetPreprocessing operator.
 * 
 */
public class TransparentAttributeFilter extends AbstractAttributeFilterCondition {


	@Override
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError {
		return ScanResult.KEEP;
	}

}
