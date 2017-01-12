package io.sugo.pio.engine.ur.detail.data

import org.apache.spark.rdd.RDD

case class DetailTrainingData(trainingData: RDD[(Int, Int)], itemData: RDD[Map[String, AnyRef]])
