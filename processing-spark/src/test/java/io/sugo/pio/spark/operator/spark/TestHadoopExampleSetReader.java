//package io.sugo.pio.spark.operator.spark;
//
//import io.sugo.pio.operator.io.AbstractReader;
//import io.sugo.pio.ports.OutputPort;
//import io.sugo.pio.ports.impl.OutputPortImpl;
//import io.sugo.pio.spark.datahandler.HadoopExampleSet;
//
///**
// */
//public class TestHadoopExampleSetReader extends AbstractReader<HadoopExampleSet> {
//    public TestHadoopExampleSetReader(OutputPort outputPort) {
//        super(HadoopExampleSet.class, "TestHadoopExampleSetReader", outputPort);
//    }
//
//    @Override
//    public HadoopExampleSet read() {
//        return new HadoopExampleSet(null);
//    }
//}
