package io.sugo.pio.engine.bbs.engine

import java.util
import io.sugo.pio.engine.data.input.{BatchEventHose}
import io.sugo.pio.engine.bbs.data.BbsTrainingData
import io.sugo.pio.engine.bbs.eval.{BbsEvalActualResult, BbsEvalQuery}
import io.sugo.pio.engine.bbs.params.BbsDatasourceParams
import io.sugo.pio.engine.training.{DataSource}
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import scala.collection.JavaConverters._

/**
  */
class BbsDatasource(batchEventHose: BatchEventHose, batchEventHoseItem: BatchEventHose, dataSourceParams: BbsDatasourceParams) extends DataSource[BbsTrainingData, BbsEvalQuery, BbsEvalActualResult]{
  val logger = Logger.getLogger(this.getClass)
  override def readTraining(sc: JavaSparkContext): BbsTrainingData = {
    val itemRDDs = batchEventHoseItem.find(sc).rdd
      .map(event => event.getProperties.asScala.toMap)
      .map(mp => (mp("itemname").asInstanceOf[String], mp("descrip").asInstanceOf[String], "item:"+ mp("itemname").toString + mp("item_url") ))
    val itemNumber = itemRDDs.count()
//    val sampleNum = dataSourceParams.sampleNum
    val sampleNum = 1000
    val sublabel_key = Constants.SUB_LABEL
    val title_key = Constants.ITEM_TITLE
    val content_key = Constants.ITEM_CONTENT
    val circle_label = dataSourceParams.labels
    val datardd = batchEventHose.find(sc).rdd
      .map(event => event.getProperties.asScala.toMap)
      .filter(keys => circle_label.contains(keys(sublabel_key)))
      .filter(_ != null)
      .map(mp => (mp(sublabel_key).asInstanceOf[String],mp(title_key).asInstanceOf[String], mp(content_key).asInstanceOf[String], mp("item_url").asInstanceOf[String]))
      .filter{ case(label, title, content, url) =>
        (label != null) && (title!=null)  && (title != "") && (label != "")
      }
      .filter(s => !(s._2.contains("删")) )

    val sampleData = meanSampleData(sc, datardd, sampleNum)
    val articleData = sampleData.map{ case(label, title, content, url) =>
      (title, content, "article:" + title + url)
    }
    val corpusData = itemRDDs.union(articleData).repartition(1)
    new BbsTrainingData(corpusData, itemNumber)
}

  def meanSampleData(sc: JavaSparkContext, data: RDD[(String, String, String, String)], num: Int): RDD[(String, String, String, String)] ={
    val labels = data.map(_._1).collect().toSet
    var cntFlag = 0
    var dataArray: Array[(String, String, String, String)] = null
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

  override def readEval(sc: JavaSparkContext): util.List[(BbsTrainingData, BbsEvalQuery, BbsEvalActualResult)] = {
    null
  }
}
