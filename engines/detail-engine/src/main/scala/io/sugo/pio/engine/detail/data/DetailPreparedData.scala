package io.sugo.pio.engine.detail.data

import org.apache.mahout.math.indexeddataset.IndexedDataset
import org.apache.spark.rdd.RDD

case class DetailPreparedData(trainingData: IndexedDataset)
