package io.sugo.pio.engine.als.engine

import java.util

import io.sugo.pio.engine.als.Constants
import io.sugo.pio.engine.als.data.ALSTrainingData
import io.sugo.pio.engine.als.eval.{ALSEvalActualResult, ALSEvalQuery}
import io.sugo.pio.engine.data.input.BatchEventHose
import io.sugo.pio.engine.training.DataSource
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

import collection.JavaConverters._

class ALSDataSource(batchEventHose: BatchEventHose) extends DataSource[ALSTrainingData, ALSEvalQuery, ALSEvalActualResult] with Serializable {
  val logger = Logger.getLogger(this.getClass)
  override def readTraining(javaSparkContext: JavaSparkContext): ALSTrainingData = {
    val sc = javaSparkContext.sc
    val events = batchEventHose.find(sc).rdd
    val trainingData = events.map({
      e => Rating(e.getProperties.get(Constants.USER_ID).toString.toInt,
        e.getProperties.get(Constants.ITEM_ID).toString.toInt,
        e.getProperties.get(Constants.GRADE).toString.toFloat)
    })
    ALSTrainingData(trainingData)
  }

  override def readEval(sc: JavaSparkContext): util.List[(ALSTrainingData, ALSEvalQuery, ALSEvalActualResult)] = {
    val events = batchEventHose.find(sc).rdd
    val trainingData = events.map({
      e => Rating(e.getProperties.get(Constants.USER_ID).asInstanceOf[Int],
        e.getProperties.get(Constants.ITEM_ID).asInstanceOf[Int],
        e.getProperties.get(Constants.GRADE).asInstanceOf[Float])
    })
    val eval = splitData(trainingData).asJava
    eval
  }

  def splitData(td: RDD[Rating]): List[(ALSTrainingData, ALSEvalQuery, ALSEvalActualResult)] = {
    val evalQueryNum = 20
    val kFold = 2
    val wholeData = td
    val zipWholeData = wholeData.zipWithUniqueId()
    logger.info("get whole data")

    val evaldata =  (0 until kFold).map { idx => {
      val trainingRatings = zipWholeData.filter(_._2 % kFold != idx).map(_._1)
      val testingRatings = zipWholeData.filter(_._2 % kFold == idx).map(_._1)

      val testingUsers: RDD[(Int, Iterable[Rating])] = testingRatings.groupBy(_.user)
      val qt = testingUsers.map{ case (user, ratings) =>
        val q = (user, evalQueryNum)
        val ar = ratings.toArray
        (q, ar)
      }
      val query = qt.map(s => s._1)
      val actResult = qt.map(s => s._2)
      (new ALSTrainingData(trainingRatings), ALSEvalQuery(query), ALSEvalActualResult(actResult))
    }}
    evaldata.toList
  }
}
