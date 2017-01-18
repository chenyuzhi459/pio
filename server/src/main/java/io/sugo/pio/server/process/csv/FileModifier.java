package io.sugo.pio.server.process.csv;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorGroup;

public class FileModifier extends Operator {
    @Override
    public String getFullName() {
        return "修改文件";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return "修改文件 for test";
    }
}
