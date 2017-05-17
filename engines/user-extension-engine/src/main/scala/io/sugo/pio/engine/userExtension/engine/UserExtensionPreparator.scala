package io.sugo.pio.engine.userExtension.engine

import java.util

import io.sugo.pio.engine.userExtension.data.{UserExtensionPreparaData, UserExtensionTrainingData}
import io.sugo.pio.engine.training.{Params, Preparator}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.stat.MultivariateStatisticalSummary
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  */
class UserExtensionPreparator() extends Preparator[UserExtensionTrainingData, UserExtensionPreparaData] with java.io.Serializable {
  override def prepare(sc: JavaSparkContext, td: UserExtensionTrainingData): UserExtensionPreparaData = {
    // 格式化数据
    val userProfileFormatData = td.data.map(
      (v: Map[String, AnyRef]) => {
        val values: util.ArrayList[AnyRef] = v("values").asInstanceOf[util.ArrayList[AnyRef]]
        val valuesList: Array[Double] = Array.range(0, values.size()).map((i: Int) => {values.get(i).toString.toDouble})
        val label: String = v("label").asInstanceOf[String]
        val userId: String = v("key").asInstanceOf[String]
        (label, userId, valuesList)
      }
    ).map(
      v => (v._1, v._2, Vectors.dense(v._3))
    )
    // 特征权重
    val featureNum: Int = userProfileFormatData.first()._3.size
    var featureWeight = td.featureWeight
    if (featureWeight == null || featureWeight.isEmpty) {
      featureWeight = new mutable.HashMap[Int, Double]
      (0 until featureNum).foreach(featureWeight.update(_, 1.0/featureNum.toDouble))
    }
    // 求各个维度数据的分布
    val (mean: Vector, variance: Vector) = mean_var(userProfileFormatData.map(v => v._3))
    val summary = mean.toArray.zip(variance.toArray.map(v => math.sqrt(v)))
    // 根据分布平滑数据
    val smoothValue: Int = 100 // 平滑范围(0 - smoothValue * featureWeight)
    val userProfileSmoothData = userProfileFormatData.map(
      v => (
        v._1,
        v._2,
//        v._3.toArray.zip(summary).map(v => gaussianDistribution(v._1.toInt, v._2._1, v._2._2, 0, smoothValue).toDouble)
        v._3.toArray.zip(summary).zipWithIndex.map(
          v => gaussianDistribution(v._1._1.toInt, v._1._2._1, v._1._2._2, 0, (smoothValue.toDouble * featureWeight(v._2)).toInt).toDouble
        )
      )
    ).map(
      // 每个向量末尾都曾加一列值, 用来做空位补齐
      v => (
        v._1,
        v._2,
//        Vectors.dense(v._3 :+ uniformDistribution(smoothValue * v._3.length - v._3.sum.toInt, 0, smoothValue * v._3.length, 0, smoothValue).toDouble)
        Vectors.dense(v._3 :+ (smoothValue - v._3.sum.toInt).toDouble)
      )
    )

    val userProfileTarget = userProfileSmoothData.filter(v => v._1 == "target").map(v => (v._2, v._3))
    val userProfileCandidate = userProfileSmoothData.filter(v => v._1 == "candidate").map(v => (v._2, v._3))
//    // 打印出来看看
//    for (v <- userProfileTarget.collect()) {
//      println("xxxxxxx: %s,%s".format(v._1, v._2.toString))
//    }

    new UserExtensionPreparaData(userProfileTarget, userProfileCandidate)
  }

  def mean_var(data: RDD[Vector]): (Vector, Vector) = {
    val matrix: RowMatrix = new RowMatrix(data)
    val statSummary: MultivariateStatisticalSummary = matrix.computeColumnSummaryStatistics()
    (statSummary.mean, statSummary.variance)
  }

  def binomialDistribution(value: Double, threshold: Double, pvalue1: Int, pvalue2: Int): Int = {
    var pvalue: Int = pvalue1
    if (value > threshold) {
      pvalue = pvalue2
    }
    pvalue
  }

  def uniformDistribution(value: Int, min1: Int, max1: Int, min2: Int, max2: Int): Int = {
    var valueNew: Int = value
    var valueUniform: Int = 0
    if (value > max1) {
      valueNew = max1
    } else if (value < min1) {
      valueNew = min1
    }
    val ratio: Float = (max2 - min2).toFloat / (max1 - min1).toFloat
    Math.round((valueNew - min1).toFloat * ratio).toInt
  }

  def gaussianDistribution(value: Int, mean: Double, std: Double, min:Int, max:Int): Int = {
    val confidence = std * 3
    val min1: Int = math.floor(mean - confidence).toInt
    val max1: Int = math.round(mean + confidence).toInt
    uniformDistribution(value, min1, max1, min, max)
  }
}

