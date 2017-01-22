package io.sugo.pio.engine.search.engine

import java.util
import java.util.{List => jList}

import io.sugo.pio.engine.search.data.{SearchModelData, SearchPreparaData}
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.search.Constants
import io.sugo.pio.engine.training.Algorithm
import org.ansj.app.keyword.KeyWordComputer
import org.ansj.splitWord.analysis.{IndexAnalysis, NlpAnalysis}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}

import scala.collection.JavaConverters._
import org.apache.spark.util.Utils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class SearchAlgorithm() extends Algorithm[SearchPreparaData, SearchModelData]{
  override def train(sc: JavaSparkContext, pd: SearchPreparaData): SearchModelData = {
    new SearchModelData(pd.preData)
  }
}

