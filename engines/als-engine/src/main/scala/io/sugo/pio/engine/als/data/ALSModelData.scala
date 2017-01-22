package io.sugo.pio.engine.als.data

import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

case class ALSModelData(model: RDD[(Int, Array[Rating])])

