package io.sugo.pio.engine.userFeatureExtractionNew.module

import java.util

import io.sugo.pio.engine.training.Preparator
import io.sugo.pio.engine.userFeatureExtractionNew.data.{UserFeatureExtractionPrepareData, UserFeatureExtractionTrainingData}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by penghuan on 2017/4/24.
  */
class UserFeatureExtractionPreparator extends Preparator[UserFeatureExtractionTrainingData, UserFeatureExtractionPrepareData] {
  override def prepare(sc: JavaSparkContext, td: UserFeatureExtractionTrainingData): UserFeatureExtractionPrepareData = {
    val rdd: RDD[LabeledPoint] = td.data.map(
      (v: Map[String, AnyRef]) => {
        val values: util.List[AnyRef] = v("values").asInstanceOf[util.List[AnyRef]]
        val valuesList: Array[Double] = Array.range(0, values.size()).map((i: Int) => {values.get(i).toString.toDouble})
        val label: String = v("label").asInstanceOf[String]
        val userId: String = v("key").asInstanceOf[String]
        (label, userId, valuesList)
      }
    ).filter(
      (v: (String, String, Array[Double])) => {
        v._1 match {
          case "target" => true
          case "candidate" => true
          case _ => false
        }
      }
    ).map(
      (v: (String, String, Array[Double])) => {
        val label: Double = v._1 match {
          case "target" => 1.0
          case "candidate" => 0.0
        }
        val features: Vector = Vectors.dense(v._3)
        new LabeledPoint(label, features)
      }
    )
    UserFeatureExtractionPrepareData(rdd)
  }
}
