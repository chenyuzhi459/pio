package io.sugo.pio.engine.detail.data

import org.apache.spark.rdd.RDD

case class DetailTrainingData(trainingData: RDD[(Int, Int)])


