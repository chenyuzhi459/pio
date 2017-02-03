package io.sugo.pio.engine.userHistory.data

import org.apache.spark.rdd.RDD

case class UserHistoryTrainingData(actData: RDD[(String, String, Long)])
