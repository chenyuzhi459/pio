package io.sugo.pio.engine.userFeatureExtraction.data

import org.apache.spark.rdd.RDD

/**
  * Created by penghuan on 2017/4/7.
  */
case class UserFeatureExtractionTrainingData(data: RDD[Map[String, AnyRef]]) {

}
