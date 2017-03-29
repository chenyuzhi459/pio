package io.sugo.pio.engine.flow.engine

import io.sugo.pio.engine.flow.data.{FlowModelData, FlowPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.spark.api.java.JavaSparkContext

class FlowAlgorithm() extends Algorithm[FlowPreparaData, FlowModelData]{
  override def train(sc: JavaSparkContext, pd: FlowPreparaData): FlowModelData = {
    new FlowModelData()
  }
}

