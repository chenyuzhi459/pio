package io.sugo.pio.engine.als

import io.sugo.pio.engine.als.data.{ALSModelData, ALSPreparedData, ALSTrainingData}
import io.sugo.pio.engine.als.engine.{ALSAlgorithm, ALSDModel, ALSDataSource, ALSPreparator}
import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.spark.engine._
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}

class ALSEngineFactory(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends EngineFactory[ALSTrainingData, ALSPreparedData, ALSModelData, QueryableModelData] {
  override def createDatasource(): DataSource[ALSTrainingData] = {
    val dataSource = new ALSDataSource(propertyHose, batchEventHose)
    dataSource
  }

  override def createPreparator(): Preparator[ALSTrainingData, ALSPreparedData] = {
    val preparator = new ALSPreparator();
    preparator
  }

  override def createAlgorithm(): Algorithm[ALSPreparedData, ALSModelData] = {
    val algorithm = new ALSAlgorithm()
    algorithm
  }

  override def createModel(): Model[ALSModelData, QueryableModelData] = {
    val model = new ALSDModel
    model
  }
}
