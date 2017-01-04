package io.sugo.pio.spark.transfer.model;

/**
 */
public class LessEqualsSplitConditionTO extends SplitConditionTO {
    private double value;

    public LessEqualsSplitConditionTO(String attributeName, double value) {
        super(attributeName);
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
