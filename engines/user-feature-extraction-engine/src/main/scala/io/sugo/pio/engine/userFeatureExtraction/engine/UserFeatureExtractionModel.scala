package io.sugo.pio.engine.userFeatureExtraction.engine

import java.io.{File, PrintWriter}

import io.sugo.pio.engine.common.data.UserExtensionRepository
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.training.Model
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionModelData
import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionType._
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.mutable

/**
  * Created by penghuan on 2017/4/7.
  */
class UserFeatureExtractionModel(val repository: Repository) extends Model[UserFeatureExtractionModelData]{
  override def save(jsc: JavaSparkContext, md: UserFeatureExtractionModelData): Unit = {
    val model = md.model
    val modelPath = repository.asInstanceOf[UserExtensionRepository].getModelFeaturePath
    model.save(JavaSparkContext.toSparkContext(jsc), modelPath)

    saveFeatureWeight(repository, md.featureWeight)
    saveFeatureDesign(repository, md.featureDesign)
  }

  def saveFeatureWeight(repository: Repository, featureWeight: TFeatureWeight): Unit = {
    val featurePath = repository.asInstanceOf[UserExtensionRepository].getPredictFeatureFile
    val writer = new PrintWriter(new File(featurePath))
    featureWeight.keys.toArray.sorted.foreach(
      id => writer.println("%d:%.4f".format(id, featureWeight(id)))
    )
    writer.flush()
    writer.close()
  }

  def saveFeatureDesign(repository: Repository, featureDesign: TFeatureDesign): Unit = {
    val designPath = repository.asInstanceOf[UserExtensionRepository].getPredictDesignFile
    val writer = new PrintWriter(new File(designPath))
    featureDesign.foreach(
      (fBranch: FBranch) => writer.println(fBranch.toJSONString)
    )
    writer.flush()
    writer.close()
  }
}
