package io.sugo.pio.engine.search.data

import org.apache.spark.rdd.RDD
import scala.collection.mutable.ArrayBuffer

case class SearchPreparaData(preData: RDD[(String, String)])