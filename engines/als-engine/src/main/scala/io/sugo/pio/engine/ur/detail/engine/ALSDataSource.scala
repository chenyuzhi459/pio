package io.sugo.pio.engine.ur.detail.engine

import io.sugo.pio.engine.ur.detail.data.{ALSTrainingData, Rating}
import io.sugo.pio.spark.engine.DataSource
import io.sugo.pio.spark.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import scala.collection.JavaConverters._

class ALSDataSource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[ALSTrainingData] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): ALSTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd
    val sqlContex = new SQLContext(sc)
    import sqlContex.implicits._
    val trainingData = events.map({
      e => Rating(e.getProperties.get("userId").asInstanceOf[Int],
        e.getProperties.get("movieId").asInstanceOf[Int],
        e.getProperties.get("rating").asInstanceOf[Float])
    }).toDS()
    val itemRdd = propertyHose.find(sc).rdd.map(s => s.asScala.toMap)
    ALSTrainingData(trainingData, itemRdd)
  }
}
