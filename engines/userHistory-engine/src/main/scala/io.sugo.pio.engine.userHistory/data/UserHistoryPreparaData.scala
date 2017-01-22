package io.sugo.pio.engine.userHistory.data

import org.apache.spark.rdd.RDD
import scala.collection.mutable.ArrayBuffer

case class UserHistoryPreparaData(preData: RDD[(String, String, Long)])