package io.sugo.pio.engine.articleClu.engine

import org.ansj.splitWord.analysis._
import org.ansj.recognition.impl.StopRecognition
import org.apache.spark.mllib.feature.{Normalizer, Word2VecModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vector

import scala.io.Source
import scala.collection.JavaConverters._

/**
  */
class Ansjpart() extends Serializable{
  def splitWord(str: String): Array[String] ={
    val parse = ToAnalysis.parse(str).recognition(stopDic)
    val words = parse.getTerms.asScala.toArray.map(s => s.getName).map(str => str.trim)
      .filter{ string =>
        (string != null) && (string != "") && (string != " ")
      }
    words
  }

  def stopDic: StopRecognition ={
    val stopPath = "dic/stopWordmmw.txt"
    var fitler:StopRecognition = new StopRecognition();
    val inputStream = this.getClass.getClassLoader.getResource(stopPath).getPath
    val lines = Source.fromFile(inputStream).getLines()
    while (lines.hasNext){
      fitler.insertStopWords(lines.next())
    }
    fitler
  }

  def text2vec(text: String, w2vModel: Word2VecModel, toAnalysis: ToAnalysis): Vector = {
    val parse = toAnalysis.parseStr(text)
    val words = parse.getTerms.asScala.toArray.map(s => s.getName.trim)
      .filter{ string =>
        (string != null) && (string != "") && (string != " ")
      }
    var flag = 1
    import breeze.linalg.Vector
    var sumVector:Vector[Double] = null

    for (word:String <- words){
      var vec :Vector[Double] = null
      try{
        vec = Vector(w2vModel.transform(word).toArray)
      }
      catch{
        case ex:IllegalStateException =>{ }
      }
      finally {
          if (vec != null){
            if (1 == flag ){
              sumVector = vec
            }
            else {
              sumVector = sumVector + vec
            }
            flag = flag + 1
          }
        }
      }
    val normalizerObj = new Normalizer()
    if (sumVector != null)
      {
        val normalResult = normalizerObj.transform(Vectors.dense(sumVector.toArray))
        normalResult
      }
    else {
      null
    }

  }

}
