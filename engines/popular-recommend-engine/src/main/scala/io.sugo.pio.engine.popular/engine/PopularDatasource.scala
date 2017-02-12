package io.sugo.pio.engine.popular.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.popular.data.PopularTrainingData
import io.sugo.pio.engine.popular.eval.{PopularEvalActualResult, PopularEvalQuery}
import io.sugo.pio.engine.training.DataSource

import scala.collection.JavaConverters._

class PopularDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[PopularTrainingData, PopularEvalQuery, PopularEvalActualResult] {

  override def readTraining(sc: JavaSparkContext): PopularTrainingData = {
    val actionRdd = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
    val itemRdd = propertyHose.find(sc).rdd.map(s => s.asScala.toMap)
    new PopularTrainingData(actionRdd, itemRdd)
  }

  override def readEval(sc: JavaSparkContext): util.List[(PopularTrainingData, PopularEvalQuery, PopularEvalActualResult)] = {
    null
  }
}


