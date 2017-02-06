package io.sugo.pio.engine.textSimilar.data

import org.apache.spark.rdd.RDD

/**
  */
case class TextSimilarTrainingData (td: RDD[(String, String, String)])