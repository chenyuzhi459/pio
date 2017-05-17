package io.sugo.pio.engine.userFeatureExtraction.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.training.DataSource
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionTrainingData
import io.sugo.pio.engine.userFeatureExtraction.eval.{UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalIndicators, UserFeatureExtractionEvalQuery}
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._

/**
  * Created by penghuan on 2017/4/7.
  */
class UserFeatureExtractionDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult] {
  override def readTraining(sc: JavaSparkContext): UserFeatureExtractionTrainingData = {
    val userProfileData = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
    new UserFeatureExtractionTrainingData(userProfileData)
  }

  override def readEval(sc: JavaSparkContext): util.List[(UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult)] = {
    null
  }
}
