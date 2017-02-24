package io.sugo.pio.engine.articleClu.data

import org.apache.spark.rdd.RDD

/**
  */
case class ArtiClusterTrainingData(td: RDD[(String, String, String)])