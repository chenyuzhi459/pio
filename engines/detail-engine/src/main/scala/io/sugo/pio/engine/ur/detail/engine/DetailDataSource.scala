package io.sugo.pio.engine.ur.detail.engine

import io.sugo.pio.engine.ur.detail.data.DetailTrainingData
import io.sugo.pio.spark.engine.DataSource
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.sql.SQLContext

import scala.collection.JavaConverters._

class DetailDataSource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[DetailTrainingData] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): DetailTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd.map(s => (s.getProperties.get("userId").asInstanceOf[Int], s.getProperties.get("movieId").asInstanceOf[Int]))
    val itemRdd = propertyHose.find(sc).rdd.map(s => s.asScala.toMap)
    DetailTrainingData(events, itemRdd)
  }
}
