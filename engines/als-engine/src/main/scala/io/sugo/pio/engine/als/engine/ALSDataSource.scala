package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.als.data.ALSTrainingData
import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.training.DataSource
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.sql.SQLContext

import scala.collection.JavaConverters._

class ALSDataSource(batchEventHose: BatchEventHose) extends DataSource[ALSTrainingData] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): ALSTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd
    val sqlContex = new SQLContext(sc)
    val trainingData = events.map({
      e => Rating(e.getProperties.get("userId").asInstanceOf[Int],
        e.getProperties.get("movieId").asInstanceOf[Int],
        e.getProperties.get("rating").asInstanceOf[Float])
    })
    ALSTrainingData(trainingData)
  }
}
