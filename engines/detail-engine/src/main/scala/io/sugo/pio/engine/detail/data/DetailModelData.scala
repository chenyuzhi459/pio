package io.sugo.pio.engine.detail.data

import org.apache.spark.rdd.RDD

case class DetailModelData(model: RDD[(String, Seq[(String, Double)])])


