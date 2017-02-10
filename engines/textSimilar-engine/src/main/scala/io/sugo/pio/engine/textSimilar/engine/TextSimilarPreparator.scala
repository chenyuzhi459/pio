package io.sugo.pio.engine.textSimilar.engine

import io.sugo.pio.engine.textSimilar.data.{TextSimilarPreparaData, TextSimilarTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class TextSimilarPreparator extends Preparator[TextSimilarTrainingData, TextSimilarPreparaData]{
  override def prepare(sc: JavaSparkContext, td: TextSimilarTrainingData): TextSimilarPreparaData = {
    new TextSimilarPreparaData(td.td)
  }
}
