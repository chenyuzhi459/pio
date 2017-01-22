package io.sugo.pio.engine.search.engine

import io.sugo.pio.engine.search.data.{SearchPreparaData, SearchTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class SearchPreparator extends Preparator[SearchTrainingData, SearchPreparaData] {
  override def prepare(sc: JavaSparkContext, td: SearchTrainingData): SearchPreparaData = {
    new SearchPreparaData(td.sessionData)
  }
}
