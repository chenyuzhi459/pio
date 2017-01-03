package io.sugo.pio.spark.operator.spark;

import io.sugo.pio.operator.OperatorDescription;
import io.sugo.pio.operator.io.AbstractReader;
import io.sugo.pio.spark.datahandler.HadoopExampleSet;

/**
 */
public class TestHadoopExampleSetReader extends AbstractReader<HadoopExampleSet> {
    public TestHadoopExampleSetReader(OperatorDescription description) {
        super(description, HadoopExampleSet.class);
    }

    @Override
    public HadoopExampleSet read() {
        return new HadoopExampleSet(null);
    }
}
