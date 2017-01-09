package io.sugo.pio.engine.ur.detail.engine

import java.util.{List => jList}
import io.sugo.pio.engine.ur.detail.data.{ALSModelData, ALSPreparedData, Rating}
import io.sugo.pio.spark.engine.Algorithm
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.ml.recommendation.ALS
import scala.collection.JavaConverters._

class ALSAlgorithm extends Algorithm[ALSPreparedData, ALSModelData] {
  val logger = Logger.getLogger(this.getClass)

  override def train(sc: JavaSparkContext, pd: ALSPreparedData): ALSModelData = {
    val trainingData = pd.trainingData
    val als = new ALS()
      .setMaxIter(5)
      .setRegParam(0.01)
      .setUserCol("userId")
      .setItemCol("itemId")
      .setRatingCol("rating")
    val model = als.fit(trainingData)
    val itemInfo = pd.itemData.map({
      m =>
        (m("movieId").asInstanceOf[Int], m("tags").asInstanceOf[jList[String]].asScala.toList)
    })
    val modelData = model.transform(trainingData).rdd
      .filter(
        r => r.getFloat(3) > 4
      )
      .map(r => (r.getInt(1), r))
      .join(itemInfo)
      .map(
        i => (i._2._1.getInt(0), (i._1, i._2._1.getFloat(3), i._2._2))
      )
      .groupByKey()

    new ALSModelData(modelData, trainingData)
  }
}
