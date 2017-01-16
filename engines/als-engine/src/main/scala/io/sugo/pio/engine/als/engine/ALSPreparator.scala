package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.als.data.{ALSPreparedData, ALSTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

class ALSPreparator extends Preparator[ALSTrainingData, ALSPreparedData] {
  override def prepare(sc: JavaSparkContext, td: ALSTrainingData): ALSPreparedData = {
    new ALSPreparedData(td.trainingData)
  }
}
