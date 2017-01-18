package io.sugo.pio.server.process.csv;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorGroup;

public class FileWriter extends Operator {
    @Override
    public String getFullName() {
        return "写文件";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return "写文件 for test";
    }
}
