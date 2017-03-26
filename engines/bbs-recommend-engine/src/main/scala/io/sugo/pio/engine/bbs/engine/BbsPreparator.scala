package io.sugo.pio.engine.bbs.engine

import io.sugo.pio.engine.bbs.data.{BbsPreparaData, BbsTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */


class BbsPreparator extends Preparator[BbsTrainingData, BbsPreparaData] with Serializable{
  override def prepare(sc: JavaSparkContext, td: BbsTrainingData): BbsPreparaData = {
    val data = td.td
    val tokenizer = new AnsjpartBbs()
    val idkeyWordRDD = data.map{ text =>
      var title = text._1
      var content = text._2
      val itemid = text._3
      var allcontent = title
      if (content != null){
        allcontent = title + content
      }
      val words =  tokenizer.splitWord(allcontent).toSeq
      (itemid, words)
     }
      .cache()

    new BbsPreparaData(idkeyWordRDD, td.itemNumber)
  }

}
