package io.sugo.pio.engine.als.data

import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD


case class ALSPreparedData(trainingData: RDD[Rating], itemData: RDD[Map[String, AnyRef]])
