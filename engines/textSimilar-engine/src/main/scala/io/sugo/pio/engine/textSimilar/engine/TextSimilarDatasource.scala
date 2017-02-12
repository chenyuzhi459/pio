package io.sugo.pio.engine.textSimilar.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.textSimilar.data.TextSimilarTrainingData
import io.sugo.pio.engine.textSimilar.eval.{TextSimilarEvalActualResult, TextSimilarEvalQuery}
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._

/**
  */
class TextSimilarDatasource (propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[TextSimilarTrainingData, TextSimilarEvalQuery, TextSimilarEvalActualResult]{
  override def readTraining(sc: JavaSparkContext): TextSimilarTrainingData = {
    val datardd = batchEventHose.find(sc).rdd
      .map(event => event.getProperties.asScala.toMap)
      .map(mp => (mp(Constants.ITEM_NAME).toString, mp(Constants.ITEM_CONTENT).toString, mp(Constants.ITEM_ID).toString))

    new TextSimilarTrainingData(datardd)
  }

  override def readEval(sc: JavaSparkContext): util.List[(TextSimilarTrainingData, TextSimilarEvalQuery, TextSimilarEvalActualResult)] = {
    null
  }
}
