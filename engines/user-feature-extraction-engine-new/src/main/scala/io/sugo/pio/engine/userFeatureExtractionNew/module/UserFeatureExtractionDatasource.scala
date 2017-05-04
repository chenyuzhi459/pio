package io.sugo.pio.engine.userFeatureExtractionNew.module

import java.util

import com.typesafe.config.Config
import io.sugo.pio.engine.common.data.Event
import io.sugo.pio.engine.training.DataSource
import io.sugo.pio.engine.userFeatureExtractionNew.data.UserFeatureExtractionTrainingData
import io.sugo.pio.engine.userFeatureExtractionNew.eval.{UserFeatureExtractionEvalActualResult, UserFeatureExtractionEvalQuery}
import io.sugo.pio.engine.userFeatureExtractionNew.tools.UserFeatureExtractionProperty
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._

/**
  * Created by penghuan on 2017/4/24.
  */
class UserFeatureExtractionDatasource(config: Config) extends DataSource[UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult] {
  override def readTraining(jsc: JavaSparkContext): UserFeatureExtractionTrainingData = {
    val sc: SparkContext = JavaSparkContext.toSparkContext(jsc)
    val inputDir: String = config.getString(UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.Input))
    val data: RDD[Map[String, AnyRef]] = sc.textFile(inputDir)
      .map(Event.deserialize)
      .map(_.getProperties.asScala.toMap)
    UserFeatureExtractionTrainingData(data)
  }

  override def readEval(jsc: JavaSparkContext): util.List[(UserFeatureExtractionTrainingData, UserFeatureExtractionEvalQuery, UserFeatureExtractionEvalActualResult)] = {
    null
  }
}
