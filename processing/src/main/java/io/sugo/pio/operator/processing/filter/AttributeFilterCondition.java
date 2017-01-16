package io.sugo.pio.operator.processing.filter;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.ports.InputPort;

import java.util.List;

/**
 * Created by root on 17-1-13.
 */
public interface AttributeFilterCondition {

    void init(ParameterHandler operator) throws UserError;

    /**
     * Indicates if this filter needs a data scan, i.e. an invocation of the check method for each
     * example.
     */
    boolean isNeedingScan();

    /**
     * Indicates that this filter needs a full data scan and can evaluate its condition only after
     * the full scan has been performed. If this method returns true, isNeedingScan must have
     * returned true either.
     */
    boolean isNeedingFullScan();

    /**
     * This method initializes this condition and resets all counters. It returns REMOVE, if the
     * attribute can be removed without checking examples. If it has been removed, no checking
     * during examples will occur. If it returns UNCHECKED, this Attribute Filter needs a full check
     * and hence the attribute cannot be deleted or kept. Distinguishing this is important, because
     * of the inverting, which otherwise might remove attributes although they only have been kept
     * for later checking.
     *
     * @param attribute this is the attribute, the filter will have to check for.
     * @throws ConditionCreationException
     */
    ScanResult beforeScanCheck(Attribute attribute) throws UserError;

    /**
     * This method checks the given example. During this method the filter might check data to
     * decide if attribute should be filtered out. If the condition needs a full scan before it can
     * decide, this result is ignored.
     */
    ScanResult check(Attribute attribute, Example example);

    /**
     * This method has to be invoked after a full scan has been performed if the isNeedingFullScan
     * method returns true.
     *
     * @return This method has to be restricted to return KEEP or REMOVED, but not unchecked
     */
    ScanResult checkAfterFullScan();

    /**
     * This method is used to get parameters needed by this AttributeFilter
     *
     * @param handler the parameter handler for defining dependencies
     */
    List<ParameterType> getParameterTypes(ParameterHandler operator, InputPort inPort, int... valueTypes);

}
