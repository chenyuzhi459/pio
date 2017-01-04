package io.sugo.pio.spark.transfer.model;

/**
 */
public class NominalSplitConditionTO extends SplitConditionTO {
    private String valueString;

    public NominalSplitConditionTO(String attributeName, String valueString) {
        super(attributeName);
        this.valueString = valueString;
    }

    public String getValueString() {
        return this.valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }
}
