package io.sugo.pio.engine.userHistory.engine

import java.util
import java.util.{List => jList}

import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.training.Algorithm
import io.sugo.pio.engine.userHistory.data.{UserHistoryModelData, UserHistoryPreparaData}
import org.ansj.app.keyword.KeyWordComputer
import org.ansj.splitWord.analysis.{IndexAnalysis, NlpAnalysis}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}

import scala.collection.JavaConverters._
import org.apache.spark.util.Utils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class UserHistoryAlgorithm() extends Algorithm[UserHistoryPreparaData, UserHistoryModelData]{
  override def train(sc: JavaSparkContext, pd: UserHistoryPreparaData): UserHistoryModelData = {
    new UserHistoryModelData(pd.preData)
  }
}

