package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.als.data.{ALSModelData, ALSPreparedData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.recommendation.ALS


class ALSAlgorithm extends Algorithm[ALSPreparedData, ALSModelData] {
  val logger = Logger.getLogger(this.getClass)

  override def train(sc: JavaSparkContext, pd: ALSPreparedData): ALSModelData = {
    val trainingData = pd.trainingData
    val als = new ALS()
      .setIterations(5)
    val model = als.run(trainingData)
    val modelData = model.recommendProductsForUsers(20)
    new ALSModelData(modelData)
  }
}
