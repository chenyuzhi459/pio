package io.sugo.pio.engine.userFeatureExtraction.data

import org.apache.spark.mllib.tree.model.{Node, Predict}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by penghuan on 2017/4/19.
  */
object UserFeatureExtractionType {
  object NodeDirection extends Enumeration {
    type NodeDirection = Value
    val Left, Right, None = Value
  }

  import NodeDirection._
  type TNode = Tuple2[Node, NodeDirection]
  type TBranch = ArrayBuffer[TNode]
  type TTree = ArrayBuffer[TBranch]

  type TFeatureID = Int
  type TWeight = Double
  case class FSplit(var floor: Option[Double], var upper: Option[Double]) {
    override def toString: String = {
      val floorStr: String = floor match {
        case v if v.isDefined => "%.4f".format(v.get)
        case _ => "null"
      }
      val upperStr: String = upper match {
        case v if v.isDefined => "%.4f".format(v.get)
        case _ => "null"
      }
      "\"floor\":%s,\"upper\":%s".format(floorStr, upperStr)
    }
  }
  case class FFeature(id: TFeatureID, fSplit: FSplit) {
    def toJSONString: String = {
      "{\"featureId\":%d,%s}".format(id, fSplit.toString)
    }
  }
  case class FBranch(var predict: Predict, fFeatures: mutable.HashMap[TFeatureID, FFeature]) {
    def toJSONString: String = {
      val features = fFeatures.toArray.map(v => v._2.toJSONString).mkString(",")
      "{\"predict\":%.1f,\"prob\":%.4f,\"fetures\":[%s]}".format(predict.predict, predict.prob, features)
    }
  }

  type TFeatureWeight = mutable.HashMap[TFeatureID, TWeight]
  type TFeatureDesign = ArrayBuffer[FBranch]
}
