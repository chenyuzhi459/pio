package io.sugo.pio.engine.flow.engine

import io.sugo.pio.engine.flow.data.{FlowPreparaData, FlowTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class FlowPreparator extends Preparator[FlowTrainingData, FlowPreparaData] {
  override def prepare(sc: JavaSparkContext, td: FlowTrainingData): FlowPreparaData = {
    new FlowPreparaData()
  }
}

