//package io.sugo.pio.spark.ports;
//
//import io.sugo.pio.ports.InputPort;
//import io.sugo.pio.ports.Port;
//import io.sugo.pio.ports.Ports;
//import io.sugo.pio.ports.impl.OutputPortImpl;
//
///**
// */
//public class SparkOutputPortImpl extends OutputPortImpl {
//    public SparkOutputPortImpl(Ports<? extends Port> owner, String name) {
//        super(owner, name);
//    }
//
//    @Override
//    public void connectTo(InputPort inputPort) {
//        if(this.getDestination() != inputPort) {
//            super.connectTo(inputPort);
//        }
//    }
//}
