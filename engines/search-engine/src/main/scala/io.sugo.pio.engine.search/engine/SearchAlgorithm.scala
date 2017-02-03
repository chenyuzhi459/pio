package io.sugo.pio.engine.search.engine

import io.sugo.pio.engine.search.data.{SearchModelData, SearchPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.spark.api.java.JavaSparkContext

class SearchAlgorithm() extends Algorithm[SearchPreparaData, SearchModelData]{
  override def train(sc: JavaSparkContext, pd: SearchPreparaData): SearchModelData = {
    new SearchModelData(pd.preData)
  }
}

