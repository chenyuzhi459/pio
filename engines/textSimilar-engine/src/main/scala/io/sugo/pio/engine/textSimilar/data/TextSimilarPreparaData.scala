package io.sugo.pio.engine.textSimilar.data

import org.apache.spark.rdd.RDD

/**
  */
case class TextSimilarPreparaData (pd: RDD[(String, String, String)])
