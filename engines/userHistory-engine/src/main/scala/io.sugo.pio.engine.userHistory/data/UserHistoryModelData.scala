package io.sugo.pio.engine.userHistory.data

import org.apache.spark.rdd.RDD
import scala.collection.mutable.ArrayBuffer

case class UserHistoryModelData(algData: RDD[(String, String, Long)])