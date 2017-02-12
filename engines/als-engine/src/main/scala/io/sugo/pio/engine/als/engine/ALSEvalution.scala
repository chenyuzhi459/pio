package io.sugo.pio.engine.als.engine

import io.sugo.pio.engine.als.data.ALSModelData
import io.sugo.pio.engine.als.eval.{ALSEvalActualResult, ALSEvalIndicators, ALSEvalQuery}
import io.sugo.pio.engine.training.Evalution
import org.apache.log4j.Logger
import org.apache.spark.mllib.recommendation.Rating

/**
  */
class ALSEvalution() extends Evalution[ALSModelData, ALSEvalQuery, ALSEvalActualResult, ALSEvalIndicators]{
  val logger = Logger.getLogger(this.getClass)
  override def predict(md: ALSModelData, qd: ALSEvalQuery, ar: ALSEvalActualResult): ALSEvalIndicators= {
    val starttime = System.currentTimeMillis()
    var count = 0
    var sumPesScore :Double = 0.0
    var sumRecallScore: Double = 0.0
    val queryCount = qd.qd.count()

    qd.qd.collect().foreach{ case (userId, remNum) =>
      val preResult = md.model.map(_._2).collect().filter(_(0).user == userId)(0)
      val userActResult = ar.ar.collect().filter(_(0).user == userId)(0)
      val (pesScore, recallScore) = PrecisionAtK(preResult, userActResult)
      sumPesScore += pesScore
      sumRecallScore += recallScore
      count += 1
      println(System.currentTimeMillis() - starttime+";count="+count)
    }
    val pesInd = sumPesScore/queryCount
    val recaInd = sumRecallScore/queryCount
    logger.info(s"precision value is $pesInd, recall value is $recaInd")
    val indResult = new ALSEvalIndicators(pesInd, recaInd)
    indResult
  }

  def PrecisionAtK(PredictedResult: Array[Rating], ActualResult: Array[Rating]): (Double, Double) ={
    val predictItem = PredictedResult.map(rate => rate.product)
    val ruCount = predictItem.length
    val actualItem = ActualResult.map(rate => rate.product)
    val tuCount = actualItem.length
    val test = predictItem.diff(actualItem)
    val hitCount = ruCount - predictItem.diff(actualItem).length
    var precisionScore = 0.0
    var recallScore = 0.0
    if (ruCount != 0){
      precisionScore = hitCount.toDouble/ruCount.toDouble
    }
    if (tuCount != 0){
      recallScore = hitCount.toDouble/tuCount.toDouble
    }
    (precisionScore, recallScore)
  }

}
