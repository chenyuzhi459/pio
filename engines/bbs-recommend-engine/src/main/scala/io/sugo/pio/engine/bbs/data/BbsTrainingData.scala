package io.sugo.pio.engine.bbs.data

import org.apache.spark.rdd.RDD

/**
  */
case class BbsTrainingData(td: RDD[(String, String, String)], itemNumber: Long)