package io.sugo.pio.engine.ur.detail.data

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

case class ALSModelData(model: RDD[(Int, Iterable[(Int, Float, List[String])])], training: Dataset[Rating])
