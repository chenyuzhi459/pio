package io.sugo.pio.engine.userFeatureExtractionNew.data

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by penghuan on 2017/4/24.
  */
case class UserFeatureExtractionPrepareData(data: RDD[LabeledPoint]) {}
