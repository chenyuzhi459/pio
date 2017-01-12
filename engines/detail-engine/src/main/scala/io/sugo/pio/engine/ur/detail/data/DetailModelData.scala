package io.sugo.pio.engine.ur.detail.data

import org.apache.spark.rdd.RDD

case class DetailModelData(model: RDD[(String, Seq[String])])
