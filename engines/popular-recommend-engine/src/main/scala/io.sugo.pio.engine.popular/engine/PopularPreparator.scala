package io.sugo.pio.engine.popular.engine

import io.sugo.pio.engine.popular.data.{PopularPreparaData, PopularTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class PopularPreparator extends Preparator[PopularTrainingData, PopularPreparaData] {
  override def prepare(sc: JavaSparkContext, td: PopularTrainingData): PopularPreparaData = {
    new PopularPreparaData(td.actionData, td.itemData)
  }
}
