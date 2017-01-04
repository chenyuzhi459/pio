package io.sugo.pio.spark.transfer.model;

import io.sugo.pio.spark.transfer.TransferObject;

/**
 */
public class SplitConditionTO extends TransferObject {
    private String attributeName;

    public SplitConditionTO(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
