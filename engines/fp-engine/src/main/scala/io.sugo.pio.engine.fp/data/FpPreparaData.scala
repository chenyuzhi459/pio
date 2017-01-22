package io.sugo.pio.engine.fp.data

import org.apache.spark.rdd.RDD

case class FpPreparaData(sessionData: RDD[Array[String]])