package io.sugo.pio.engine.userExtension.engine

import java.util

import io.sugo.pio.engine.common.data.UserExtensionRepository
import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.userExtension.data.UserExtensionTrainingData
import io.sugo.pio.engine.userExtension.eval.{UserExtensionEvalActualResult, UserExtensionEvalQuery}
import io.sugo.pio.engine.training.DataSource
import io.sugo.pio.engine.data.output.Repository

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.io.Source

class UserExtensionDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose, repository: Repository) extends DataSource[UserExtensionTrainingData, UserExtensionEvalQuery, UserExtensionEvalActualResult] {

  override def readTraining(sc: JavaSparkContext): UserExtensionTrainingData = {
    val userProfileData = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
    userProfileData.first()
    val featureWeight: mutable.HashMap[Int, Double] = readFeatureWeightFromLocalFile(repository)
    new UserExtensionTrainingData(userProfileData, featureWeight)
  }

  override def readEval(sc: JavaSparkContext): util.List[(UserExtensionTrainingData, UserExtensionEvalQuery, UserExtensionEvalActualResult)] = {
    null
  }

  def readFeatureWeightFromLocalFile(repository: Repository): mutable.HashMap[Int, Double] = {
    val repo: UserExtensionRepository = repository.asInstanceOf[UserExtensionRepository]
    val file: String = repo.getPredictFeatureFileName
    if (!repo.exists(file)) {return null}
    val featureWeight = new mutable.HashMap[Int, Double]()
    Source.fromFile(repo.getPredictFeatureFile).getLines().foreach(
      (line: String) => {
        val v: Array[String] = line.split(":")
        if (v.length >= 2) {
          featureWeight.put(v(0).toInt, v(1).toDouble)
        }
      }
    )
    featureWeight
  }
}


