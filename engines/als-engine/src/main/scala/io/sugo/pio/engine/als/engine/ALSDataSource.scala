package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.als.Constants
import io.sugo.pio.engine.als.data.ALSTrainingData
import io.sugo.pio.engine.data.input.BatchEventHose
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.recommendation.Rating

class ALSDataSource(batchEventHose: BatchEventHose) extends DataSource[ALSTrainingData] with Serializable {
  override def readTraining(javaSparkContext: JavaSparkContext): ALSTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd
    val trainingData = events.map({
      e => Rating(e.getProperties.get(Constants.USER_ID).asInstanceOf[Int],
        e.getProperties.get(Constants.ITEM_ID).asInstanceOf[Int],
        e.getProperties.get(Constants.GRADE).asInstanceOf[Float])
    })
    ALSTrainingData(trainingData)
  }
}
