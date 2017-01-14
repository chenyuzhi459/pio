package io.sugo.pio.engine.detail.engine

import io.sugo.pio.engine.detail.data.{DetailPreparedData, DetailTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.mahout.math.indexeddataset.BiDictionary
import org.apache.mahout.sparkbindings.indexeddataset.IndexedDatasetSpark
import org.apache.spark.api.java.JavaSparkContext

class DetailPreparator extends Preparator[DetailTrainingData, DetailPreparedData] {
  override def prepare(sc: JavaSparkContext, td: DetailTrainingData): DetailPreparedData = {

    var userDictionary: Option[BiDictionary] = None

    val actData = td.trainingData.map(
      v=> (v._1.toString, v._2.toString)
    )
    val indexedDatasets = IndexedDatasetSpark(actData, userDictionary)(sc)
    userDictionary = Some(indexedDatasets.rowIDs)
    val numUsers = userDictionary.get.size
    val rowAdjustedIds = indexedDatasets.create(indexedDatasets.matrix, userDictionary.get, indexedDatasets.columnIDs).newRowCardinality(numUsers)

    new DetailPreparedData(rowAdjustedIds)
  }
}
