package io.sugo.pio.engine.articleClu.data

import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.feature.Word2VecModel

import scala.collection.mutable

/**
  */
case class ArtiClusterModelData(clfModel: LogisticRegressionModel, w2vModel: Word2VecModel, labelMap: Map[String, Long])
