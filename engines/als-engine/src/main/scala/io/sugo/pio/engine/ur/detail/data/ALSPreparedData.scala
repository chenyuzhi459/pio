package io.sugo.pio.engine.ur.detail.data

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

case class ALSPreparedData(trainingData: Dataset[Rating], itemData: RDD[Map[String, AnyRef]])
