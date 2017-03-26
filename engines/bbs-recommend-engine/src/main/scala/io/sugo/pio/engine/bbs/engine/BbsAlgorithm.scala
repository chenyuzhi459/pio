package io.sugo.pio.engine.bbs.engine

import io.sugo.pio.engine.bbs.data.{BbsModelData, BbsPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession

/**
  */

class BbsAlgorithm extends Algorithm[BbsPreparaData, BbsModelData] with Serializable{
  val logger = Logger.getLogger(this.getClass)
  override def train(sc: JavaSparkContext, pd: BbsPreparaData): BbsModelData = {
    val wholeData = pd.pd
    val spark = SparkSession.builder().getOrCreate()
    val lsaObj = new LsaAlgorithm(wholeData,spark, sc, pd.itemNumber)
    val modeldata = lsaObj.start()
    new BbsModelData(modeldata)
  }
}
