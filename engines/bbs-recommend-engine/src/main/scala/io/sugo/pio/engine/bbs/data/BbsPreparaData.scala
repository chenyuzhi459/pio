package io.sugo.pio.engine.bbs.data

import org.apache.spark.rdd.RDD

/**
  */
case class BbsPreparaData(pd: RDD[(String, Seq[String])], itemNumber: Long)
