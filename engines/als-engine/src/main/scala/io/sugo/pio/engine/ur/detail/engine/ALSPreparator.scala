package io.sugo.pio.engine.ur.detail.engine

import io.sugo.pio.engine.ur.detail.data.{ALSPreparedData, ALSTrainingData}
import io.sugo.pio.spark.engine.Preparator
import org.apache.spark.api.java.JavaSparkContext

class ALSPreparator extends Preparator[ALSTrainingData, ALSPreparedData] {
  override def prepare(sc: JavaSparkContext, td: ALSTrainingData): ALSPreparedData = {
    new ALSPreparedData(td.trainingData, td.itemData)
  }
}
