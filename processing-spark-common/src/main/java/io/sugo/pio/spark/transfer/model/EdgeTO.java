package io.sugo.pio.spark.transfer.model;

/**
 */
public class EdgeTO {
    private SplitConditionTO splitCondition;
    private TreeTO child;

    public EdgeTO(TreeTO child, SplitConditionTO splitCondition) {
        this.child = child;
        this.splitCondition = splitCondition;
    }

    public SplitConditionTO getSplitCondition() {
        return this.splitCondition;
    }

    public void setSplitCondition(SplitConditionTO splitCondition) {
        this.splitCondition = splitCondition;
    }

    public TreeTO getChild() {
        return this.child;
    }

    public void setChild(TreeTO child) {
        this.child = child;
    }
}
