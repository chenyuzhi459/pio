package io.sugo.pio.engine.popular.engine

import io.sugo.pio.engine.common.data.QueryableModelData
import io.sugo.pio.engine.popular.data.{PopularModelData, PopularPreparaData, PopularTrainingData}
import io.sugo.pio.spark.engine._
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}

/**
  */
class PopularEngineFactory(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends EngineFactory[PopularTrainingData, PopularPreparaData, PopularModelData, QueryableModelData]{
  override def createDatasource(): DataSource[PopularTrainingData] = {
    val dataSource = new PopularDatasource(propertyHose, batchEventHose)
    dataSource
  }

  override def createPreparator(): Preparator[PopularTrainingData, PopularPreparaData] = {
    val preparator = new PopularPreparator()
    preparator
  }

  override def createAlgorithm(): Algorithm[PopularPreparaData, PopularModelData] = {
    val algorithm = new PopularAlgorithm()
    algorithm
  }

  override def createModel(): Model[PopularModelData, QueryableModelData] = {
    val model = new PopularModel
    model
  }
}