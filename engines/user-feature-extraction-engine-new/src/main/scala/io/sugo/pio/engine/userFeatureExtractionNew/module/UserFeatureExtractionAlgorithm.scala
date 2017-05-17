package io.sugo.pio.engine.userFeatureExtractionNew.module

import io.sugo.pio.engine.training.Algorithm
import io.sugo.pio.engine.userFeatureExtractionNew.data.UserFeatureExtractionType._
import io.sugo.pio.engine.userFeatureExtractionNew.data.{UserFeatureExtractionModelData, UserFeatureExtractionPrepareData}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.{DecisionTreeModel, Node, Predict}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by penghuan on 2017/4/24.
  */
class UserFeatureExtractionAlgorithm extends Algorithm[UserFeatureExtractionPrepareData, UserFeatureExtractionModelData] {
  import NodeDirection._

  override def train(sc: JavaSparkContext, pd: UserFeatureExtractionPrepareData): UserFeatureExtractionModelData = {
    // 训练决策树
    val model: DecisionTreeModel = DecisionTree.trainClassifier(pd.data, 2, Map[Int, Int](), "gini", 5, 32)

    // 从决策树中分析出各个特征的权重
    val featureWeight: TFeatureWeight = extractFeatureWeightFromDecisionTree(model, pd.data.first().features.size)

    // 从决策树中分析出各个维度指标
    val featureDesign: TFeatureDesign = extractFeatureDesignFromDecisionTree(model)

    UserFeatureExtractionModelData(model, featureWeight, featureDesign)
  }

  def flattenTreeDepthFirst(node: Node): TTree = {
    if (node.isLeaf) {
      val tNode: TNode = (node, NodeDirection.None)
      val tBranch: TBranch = ArrayBuffer(tNode)
      val tTree: TTree = ArrayBuffer(tBranch)
      return tTree
    }

    val tTree: TTree = new ArrayBuffer()
    if (node.leftNode.isDefined) {
      flattenTreeDepthFirst(node.leftNode.get).foreach(
        (tBranch: TBranch) => {
          val tNode: TNode = (node, NodeDirection.Left)
          tBranch.insert(0, tNode)
          tTree.append(tBranch)
        }
      )
    }
    if (node.rightNode.isDefined) {
      flattenTreeDepthFirst(node.rightNode.get).foreach(
        (tBranch: TBranch) => {
          val tNode: TNode = (node, NodeDirection.Right)
          tBranch.insert(0, tNode)
          tTree.append(tBranch)
        }
      )
    }
    tTree
  }

  def flattenBranch(tBranch: TBranch): FBranch = {
    val fBranch: FBranch = FBranch(null, new mutable.HashMap)
    tBranch.foreach(
      (tNode: TNode) => {
        if (tNode._1.isLeaf) {
          fBranch.predict = tNode._1.predict
        } else {
          val id: TFeatureID = tNode._1.split.get.feature
          val threshold: Double = tNode._1.split.get.threshold
          val direction: NodeDirection = tNode._2

          var fFeature: FFeature = null
          if (fBranch.fFeatures.get(id).isDefined) {
            fFeature = fBranch.fFeatures(id)
          } else {
            fFeature = FFeature(id, FSplit(Option.empty, Option.empty))
          }
          if (direction == NodeDirection.Left) {
            if (fFeature.fSplit.upper.isDefined) {
              fFeature.fSplit.upper = Option(Math.min(threshold, fFeature.fSplit.upper.get))
            } else {
              fFeature.fSplit.upper = Option(threshold)
            }
          }
          if (direction == NodeDirection.Right) {
            if (fFeature.fSplit.floor.isDefined) {
              fFeature.fSplit.floor = Option(Math.max(threshold, fFeature.fSplit.floor.get))
            } else {
              fFeature.fSplit.floor = Option(threshold)
            }
          }
          fBranch.fFeatures.update(id, fFeature)
        }
      }
    )
    fBranch
  }

  def extractFeatureWeightFromDecisionTree(model: DecisionTreeModel, featureNum: Int): TFeatureWeight = {
    // 从模型中分析出各个特征的权重
    //    1. 将决策树扁平化, 每一个分支为一个数组
    val topNode: Node = model.topNode
    val tTree: TTree = flattenTreeDepthFirst(topNode)
    //    2. 根据每个叶子节点的分类效果为每一个分支分配权值
    val total: Double = tTree.map(_.last._1.predict.prob).sum
    val tTreeWeight: ArrayBuffer[(TBranch, Double)] = tTree.zip(tTree.map(v => v.last._1.predict.prob / total))
    //    3. 根据每个分支的权值为每一个特征分配权值
    var featureWeight: TFeatureWeight = new mutable.HashMap
    (0 until featureNum).foreach(featureWeight.update(_, 0.0))
    tTreeWeight.foreach(
      (v: (TBranch, Double)) => {
        val nodeNum: Double = v._1.length.toDouble - 1.0
        val weight: Double = v._2 / nodeNum
        v._1.foreach(
          (tNode: TNode) => {
            if (tNode._1.split.isDefined) {
              val id: Int = tNode._1.split.get.feature
              val weight_old: Double = featureWeight(id)
              featureWeight.update(id, weight_old + weight)
            }
          }
        )
      }
    )
    featureWeight
  }

  def extractFeatureDesignFromDecisionTree(model: DecisionTreeModel): TFeatureDesign = {
    // 从模型中分析出重要的维度和指标
    val topNode: Node = model.topNode
    val tTree: TTree = flattenTreeDepthFirst(topNode)
    tTree.map(flattenBranch)
  }
}
