package io.sugo.pio.engine.popular.data

import org.apache.spark.rdd.RDD

/**
  */
case class PopularModelData(itemPopular: RDD[(Int, (Int, List[String]))])
