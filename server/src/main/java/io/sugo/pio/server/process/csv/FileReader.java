package io.sugo.pio.server.process.csv;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorGroup;

public class FileReader extends Operator {
    @Override
    public String getFullName() {
        return "读取文件";
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.source;
    }

    @Override
    public String getDescription() {
        return "读取文件 for test";
    }
}
