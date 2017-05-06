package io.sugo.pio.engine.userFeatureExtractionNew.data

import io.sugo.pio.engine.userFeatureExtractionNew.data.UserFeatureExtractionType.{TFeatureDesign, TFeatureWeight}
import org.apache.spark.mllib.tree.model.DecisionTreeModel

/**
  * Created by penghuan on 2017/4/24.
  */
case class UserFeatureExtractionModelData(model: DecisionTreeModel, featureWeight: TFeatureWeight, featureDesign: TFeatureDesign) {}
