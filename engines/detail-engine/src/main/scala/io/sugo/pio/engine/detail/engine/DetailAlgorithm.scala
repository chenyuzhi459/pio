package io.sugo.pio.engine.detail.engine

import io.sugo.pio.engine.detail.data.{DetailModelData, DetailPreparedData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.log4j.Logger
import org.apache.mahout.math.cf.SimilarityAnalysis
import org.apache.mahout.sparkbindings.indexeddataset.IndexedDatasetSpark
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD


class DetailAlgorithm extends Algorithm[DetailPreparedData, DetailModelData] {
  val logger = Logger.getLogger(this.getClass)

  override def train(sc: JavaSparkContext, pd: DetailPreparedData): DetailModelData = {
    val alsData = pd.trainingData

    val cooccurrenceIDSs = SimilarityAnalysis.cooccurrencesIDSs(
      Array(alsData),
      randomSeed = 10,
      maxInterestingItemsPerThing = 50,
      maxNumInteractions = 500)
      .map(_.asInstanceOf[IndexedDatasetSpark]) // strip action names

    val modelData = cooccurrenceIDSs(0)
      .toStringMapRDD()
      .asInstanceOf[RDD[(String, Seq[(String,Double)])]]
    new DetailModelData(modelData)
  }
}
