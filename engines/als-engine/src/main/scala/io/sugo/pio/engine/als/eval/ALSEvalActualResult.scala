package io.sugo.pio.engine.als.eval

import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

/**
  */
case class ALSEvalActualResult (ar: RDD[Array[Rating]])
