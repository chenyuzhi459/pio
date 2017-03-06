package io.sugo.pio.engine.articleClu.data

import org.apache.spark.mllib.feature.Word2VecModel
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

/**
  */
case class ArtiClusterPreparaData(pd: RDD[(Vector, String, String)], w2vModel: Word2VecModel)
