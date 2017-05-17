package io.sugo.pio.engine.userFeatureExtraction.data

import io.sugo.pio.engine.userFeatureExtraction.data.UserFeatureExtractionType._
import org.apache.spark.mllib.tree.model.DecisionTreeModel

import scala.collection.mutable

/**
  * Created by penghuan on 2017/4/7.
  */
case class UserFeatureExtractionModelData(model: DecisionTreeModel, featureWeight: TFeatureWeight, featureDesign: TFeatureDesign) {}
