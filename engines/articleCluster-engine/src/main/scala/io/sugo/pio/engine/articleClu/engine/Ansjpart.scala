package io.sugo.pio.engine.articleClu.engine

import org.ansj.splitWord.analysis.{BaseAnalysis, IndexAnalysis, NlpAnalysis, ToAnalysis}
import org.ansj.recognition.impl.StopRecognition
import scala.io.Source
import scala.collection.JavaConverters._

/**
  */
class Ansjpart() extends Serializable{
  def splitWord(str: String): Array[String] ={
//    val parse = ToAnalysis.parse(str).recognition(stopDic)
//    val parse = BaseAnalysis.parse(str).recognition(stopDic)
    val parse = NlpAnalysis.parse(str).recognition(stopDic)
//    val parse = IndexAnalysis.parse(str).recognition(stopDic)

    val words = parse.getTerms.asScala.toArray.map(s => s.getName)
    words
  }

  def stopDic: StopRecognition ={
    var fitler:StopRecognition = new StopRecognition();
    val inputStream = this.getClass.getClassLoader.getResource("dic/stopWordmmw.txt").getPath
    val lines = Source.fromFile(inputStream).getLines()
    while (lines.hasNext){
      fitler.insertStopWords(lines.next())
    }
    fitler
  }

}
