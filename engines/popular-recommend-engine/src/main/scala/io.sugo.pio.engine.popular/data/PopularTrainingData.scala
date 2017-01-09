package io.sugo.pio.engine.popular.data

import org.apache.spark.rdd.RDD

case class PopularTrainingData(actionData: RDD[Map[String, AnyRef]], itemData: RDD[Map[String, AnyRef]])
