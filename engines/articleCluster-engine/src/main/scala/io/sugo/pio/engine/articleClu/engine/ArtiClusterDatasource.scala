package io.sugo.pio.engine.articleClu.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.articleClu.data.ArtiClusterTrainingData
import io.sugo.pio.engine.articleClu.eval.{ArtiClusterEvalActualResult, ArtiClusterEvalQuery}
import io.sugo.pio.engine.training.DataSource
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  */
class ArtiClusterDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[ArtiClusterTrainingData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult]{
  val logger = Logger.getLogger(this.getClass)
  override def readTraining(sc: JavaSparkContext): ArtiClusterTrainingData = {
    val sampleNum = 3000
    val datardd = batchEventHose.find(sc).rdd
      .map(event => event.getProperties.asScala.toMap)
      .filter(_("parlabel") == "妈妈网")
      .filter(s =>
      s("sublabel") =="准备怀孕圈"
      || s("sublabel") =="难孕难育圈"
      || s("sublabel") =="孕8-10月圈"
      || s("sublabel") =="生男生女圈"
      || s("sublabel") =="宝宝取名圈"
      || s("sublabel") =="宝宝营养辅食"
      || s("sublabel") =="宝宝常见病圈"
      || s("sublabel") =="早教幼教圈")
      .filter(_ != null)
      .map(mp => (mp("sublabel").asInstanceOf[String], mp("title").asInstanceOf[String], mp("content").asInstanceOf[String]))
      .filter{ case(label, title, content) =>
        (label != null) && (title!=null)  && (title != "") && (label != "")
      }
      .filter(s => !(s._2.contains("删")) )

    val sampleData = meanSampleData(sc, datardd, sampleNum)
    dispStatis(sampleData)

    new ArtiClusterTrainingData(sampleData)
}

  def meanSampleData(sc: JavaSparkContext, data: RDD[(String, String, String)], num: Int): RDD[(String, String, String)] ={
    val labels = data.map(_._1).collect().toSet
    var cntFlag = 0
    var dataArray: Array[(String, String, String)] = null
    labels.foreach{ label =>
      val labelData = data.filter(_._1 == label)
        .takeSample(false, num)
      if (0 == cntFlag){
        dataArray = labelData
      }
      else {
        dataArray = dataArray ++ labelData
      }
      cntFlag = cntFlag + 1
    }
    val sampleData = sc.parallelize(dataArray.toList)
    sampleData
  }

  def dispStatis(data: RDD[(String, String, String)]): Unit ={
    val labels = data.map(_._1).collect().toSet
    val statisInf = labels.map{ label =>
      val labelData = data.filter(_._1 == label)
      val number = labelData.count()
      (label, number)
    }

    statisInf.foreach{ case(label, number) =>
      logger.info(s"the number of label($label) is $number")
    }
  }

  override def readEval(sc: JavaSparkContext): util.List[(ArtiClusterTrainingData, ArtiClusterEvalQuery, ArtiClusterEvalActualResult)] = {
    null
  }
}
