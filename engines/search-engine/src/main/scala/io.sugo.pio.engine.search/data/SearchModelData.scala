package io.sugo.pio.engine.search.data

import org.apache.spark.rdd.RDD
import scala.collection.mutable.ArrayBuffer

case class SearchModelData(algData: RDD[(String, String)])