package io.sugo.pio.engine.userExtension.data

import org.apache.spark.mllib.clustering.LDAModel
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.Vector

/**
  */
case class UserExtensionModelData(model: LDAModel, targetData: RDD[(String, Vector)], candidateData: RDD[(String, Vector)])
