package io.sugo.pio.engine.articleClu.engine

import io.sugo.pio.engine.articleClu.data.{ArtiClusterModelData, ArtiClusterPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  */

class ArtiClusterAlgorithm extends Algorithm[ArtiClusterPreparaData, ArtiClusterModelData] with Serializable{
  val logger = Logger.getLogger(this.getClass)
  override def train(sc: JavaSparkContext, pd: ArtiClusterPreparaData): ArtiClusterModelData = {
    val wholeData = pd.pd
    val labebsMap = wholeData.map(_._2).distinct().zipWithIndex().collect().toMap
    val trainData = wholeData.map{ case (vec, label, title) =>
      LabeledPoint(labebsMap(label), vec)
    }.cache()
    val clfModel = classifyModel(labebsMap, trainData)
    val w2vModel = pd.w2vModel

    new ArtiClusterModelData(clfModel, w2vModel, labebsMap)
  }

  def classifyModel(labebsMap: Map[String, Long], trainData: RDD[LabeledPoint]): LogisticRegressionModel = {
    val numClasses = labebsMap.size
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(numClasses)
      .run(trainData)
    model
  }
}
