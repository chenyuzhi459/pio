package io.sugo.pio.engine.detail.engine

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.detail.data.DetailTrainingData
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._

class DetailDataSource(batchEventHose: BatchEventHose) extends DataSource[DetailTrainingData] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): DetailTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd.map(s => (s.getProperties.get("userId").asInstanceOf[Int], s.getProperties.get("movieId").asInstanceOf[Int]))
    DetailTrainingData(events)
  }
}
