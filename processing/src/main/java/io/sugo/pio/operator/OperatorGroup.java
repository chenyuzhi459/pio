package io.sugo.pio.operator;

/**
 * Created by root on 17-1-17.
 */
public enum OperatorGroup {
    source ("source","数据源"),
    processing("processing", "数据处理"),
    algorithmModel("algorithmModel", "算法建模");

    private final String group;
    private final String description;

    OperatorGroup(String group, String description){
        this.group = group;
        this.description = description;
    }

    public String getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }
}
