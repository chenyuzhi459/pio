package io.sugo.pio.engine.articleClu.engine

import io.sugo.pio.engine.articleClu.data.{ArtiClusterModelData, ArtiClusterPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext

import scala.collection.JavaConverters._
/**
  */

class ArtiClusterAlgorithm extends Algorithm[ArtiClusterPreparaData, ArtiClusterModelData] with Serializable{
  val logger = Logger.getLogger(this.getClass)
  override def train(sc: JavaSparkContext, pd: ArtiClusterPreparaData): ArtiClusterModelData = {
    val wholeData = pd.pd
    val classifyModel = new RFModel(sc, wholeData)
    classifyModel.train()
    new ArtiClusterModelData(null)
  }
}
