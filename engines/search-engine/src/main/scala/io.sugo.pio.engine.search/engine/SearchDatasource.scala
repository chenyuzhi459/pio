package io.sugo.pio.engine.search.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.search.Constants
import io.sugo.pio.engine.search.data.SearchTrainingData
import io.sugo.pio.engine.search.eval.{SearchEvalActualResult, SearchEvalQuery}
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._

class SearchDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[SearchTrainingData, SearchEvalQuery, SearchEvalActualResult]{

  override def readTraining(sc: JavaSparkContext): SearchTrainingData = {
    val originalRDD = propertyHose.find(sc).rdd.map(s => s.asScala)
      .map(itemInf => (itemInf(Constants.ITEM_ID).toString, itemInf(Constants.ITEM_NAME).toString) )
    new SearchTrainingData(originalRDD)
  }

  override def readEval(sc: JavaSparkContext): util.List[(SearchTrainingData, SearchEvalQuery, SearchEvalActualResult)] = {
    null
  }
}

