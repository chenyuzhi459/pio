package io.sugo.pio.engine.detail.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.detail.Constants
import io.sugo.pio.engine.detail.data.DetailTrainingData
import io.sugo.pio.engine.detail.eval.{DetailEvalActualResult, DetailEvalQuery}
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._

class DetailDataSource(batchEventHose: BatchEventHose) extends DataSource[DetailTrainingData, DetailEvalQuery, DetailEvalActualResult] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): DetailTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd.map(s => (s.getProperties.get(Constants.USER_ID).asInstanceOf[Int], s.getProperties.get(Constants.ITEM_ID).asInstanceOf[Int]))
    DetailTrainingData(events)
  }

  override def readEval(sc: JavaSparkContext): util.List[(DetailTrainingData, DetailEvalQuery, DetailEvalActualResult)] = {
    null
  }
}

