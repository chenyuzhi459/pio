package io.sugo.pio.engine.fp.data

import org.apache.spark.rdd.RDD

case class FpTrainingData(sessionData: RDD[Array[String]])
