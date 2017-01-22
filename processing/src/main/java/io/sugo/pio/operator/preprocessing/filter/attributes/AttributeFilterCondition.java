package io.sugo.pio.operator.preprocessing.filter.attributes;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.MetaDataInfo;

import java.util.List;

/**
 * This interface must be implemented by classes implementing an AttributeFilterCondition for the
 * AttributeFilter operator.
 * 
 */
public interface AttributeFilterCondition {

	public static enum ScanResult {
		REMOVE, KEEP, UNCHECKED;

		public ScanResult invert(boolean invert) {
			switch (this) {
				case KEEP:
					return (invert) ? REMOVE : KEEP;
				case REMOVE:
					return (invert) ? KEEP : REMOVE;
				default:
					return UNCHECKED;
			}
		}
	};

	/**
	 * Initializes the condition before checking anything. If checking depends on parameters, their
	 * values might be retrieved in this method.
	 * 
	 * @throws UserError
	 * @throws ConditionCreationException
	 *             TODO
	 */
	public void init(ParameterHandler operator) throws UserError, ConditionCreationException;

	/**
	 * This method tries to check if the given attribute is contained, removed from the resulting
	 * operation or if the result is unpredictable.
	 * 
	 * @param attribute
	 *            the meta data of the attribute
	 * @param parameterHandler
	 *            to get the value of the defined parameters
	 * @return
	 * @throws ConditionCreationException
	 */
	public MetaDataInfo isFilteredOutMetaData(AttributeMetaData attribute, ParameterHandler parameterHandler)
			throws ConditionCreationException;

	/**
	 * Indicates if this filter needs a data scan, i.e. an invocation of the check method for each
	 * example.
	 */
	public boolean isNeedingScan();

	/**
	 * Indicates that this filter needs a full data scan and can evaluate its condition only after
	 * the full scan has been performed. If this method returns true, isNeedingScan must have
	 * returned true either.
	 */
	public boolean isNeedingFullScan();

	/**
	 * This method initializes this condition and resets all counters. It returns REMOVE, if the
	 * attribute can be removed without checking examples. If it has been removed, no checking
	 * during examples will occur. If it returns UNCHECKED, this Attribute Filter needs a full check
	 * and hence the attribute cannot be deleted or kept. Distinguishing this is important, because
	 * of the inverting, which otherwise might remove attributes although they only have been kept
	 * for later checking.
	 * 
	 * @param attribute
	 *            this is the attribute, the filter will have to check for.
	 * @throws ConditionCreationException
	 */
	public ScanResult beforeScanCheck(Attribute attribute) throws UserError;

	/**
	 * This method checks the given example. During this method the filter might check data to
	 * decide if attribute should be filtered out. If the condition needs a full scan before it can
	 * decide, this result is ignored.
	 */
	public ScanResult check(Attribute attribute, Example example);

	/**
	 * This method has to be invoked after a full scan has been performed if the isNeedingFullScan
	 * method returns true.
	 * 
	 * @return This method has to be restricted to return KEEP or REMOVED, but not unchecked
	 */
	public ScanResult checkAfterFullScan();

	/**
	 * This method is used to get parameters needed by this AttributeFilter
	 */
	public List<ParameterType> getParameterTypes(ParameterHandler operator, InputPort inPort, int... valueTypes);

}
