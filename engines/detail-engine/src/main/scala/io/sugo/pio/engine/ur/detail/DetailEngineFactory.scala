package io.sugo.pio.engine.ur.detail

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.ur.detail.data.{DetailModelData, DetailPreparedData, DetailTrainingData}
import io.sugo.pio.engine.ur.detail.engine._
import io.sugo.pio.spark.engine._
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}

class DetailEngineFactory(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends EngineFactory[DetailTrainingData, DetailPreparedData, DetailModelData, QueryableModelData] {
  override def createDatasource(): DataSource[DetailTrainingData] = {
    val dataSource = new DetailDataSource(propertyHose, batchEventHose)
    dataSource
  }

  override def createPreparator(): Preparator[DetailTrainingData, DetailPreparedData] = {
    val preparator = new DetailPreparator();
    preparator
  }

  override def createAlgorithm(): Algorithm[DetailPreparedData, DetailModelData] = {
    val algorithm = new DetailAlgorithm()
    algorithm
  }

  override def createModel(): Model[DetailModelData, QueryableModelData] = {
    val model = new DetailModel
    model
  }
}
