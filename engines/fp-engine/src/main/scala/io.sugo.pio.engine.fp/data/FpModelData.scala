package io.sugo.pio.engine.fp.data

import org.apache.spark.rdd.RDD

/**
  */
case class FpModelData(fpData: RDD[(String, Array[String], Double)])