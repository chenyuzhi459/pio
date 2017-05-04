package io.sugo.pio.engine.userFeatureExtractionNew.module

import com.typesafe.config.Config
import io.sugo.pio.engine.training.Model
import io.sugo.pio.engine.userFeatureExtractionNew.data.UserFeatureExtractionModelData
import io.sugo.pio.engine.userFeatureExtractionNew.data.UserFeatureExtractionType._
import io.sugo.pio.engine.userFeatureExtractionNew.tools.UserFeatureExtractionProperty
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext

/**
  * Created by penghuan on 2017/4/24.
  */
class UserFeatureExtractionModel(config: Config) extends Model[UserFeatureExtractionModelData] {
  override def save(jsc: JavaSparkContext, md: UserFeatureExtractionModelData): Unit = {
    val sc: SparkContext = JavaSparkContext.toSparkContext(jsc)
    val model = md.model
    val modelPath = config.getString(UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputModel))
    val weightPath = config.getString(UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureWeight))
    val designPath = config.getString(UserFeatureExtractionProperty.keys.get(UserFeatureExtractionProperty.Index.OutputFeatureDesign))

    model.save(sc, modelPath)
    saveFeatureWeight(sc, md.featureWeight, weightPath)
    saveFeatureDesign(sc, md.featureDesign, designPath)
  }

  def saveFeatureWeight(sc: SparkContext, featureWeight: TFeatureWeight, path: String): Unit = {
    sc.parallelize(
      featureWeight.keys.toArray.sorted.map(
        id => "%d:%.4f".format(id, featureWeight(id))
      )
    ).saveAsTextFile(path)
  }

  def saveFeatureDesign(sc: SparkContext, featureDesign: TFeatureDesign, path: String): Unit = {
    sc.parallelize(
      featureDesign.map(
        (fBranch: FBranch) => fBranch.toJSONString
      )
    ).saveAsTextFile(path)
  }
}
