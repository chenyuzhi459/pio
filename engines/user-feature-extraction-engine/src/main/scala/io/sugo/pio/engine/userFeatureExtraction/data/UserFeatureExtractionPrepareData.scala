package io.sugo.pio.engine.userFeatureExtraction.data

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by penghuan on 2017/4/7.
  */
case class UserFeatureExtractionPrepareData(data: RDD[LabeledPoint]) {}
