package io.sugo.pio.engine.userExtension.data

import org.apache.spark.rdd.RDD

import scala.collection.mutable


case class UserExtensionTrainingData(data: RDD[Map[String, AnyRef]], featureWeight: mutable.HashMap[Int, Double])

