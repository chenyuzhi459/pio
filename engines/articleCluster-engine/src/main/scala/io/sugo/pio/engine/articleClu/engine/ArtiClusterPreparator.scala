package io.sugo.pio.engine.articleClu.engine

import io.sugo.pio.engine.articleClu.data.{ArtiClusterPreparaData, ArtiClusterTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.feature.{Normalizer, Word2Vec, Word2VecModel}
import org.apache.spark.mllib.linalg.{DenseVector, Vector, Vectors}

/**
  */
object  word2vecPrama{
  val seed: Int = 75
  val minCount: Int = 1
  val learningRate: Double = 0.025
  val numIterations: Int = 40
  val vectorSize: Int = 100
  val minTokenSize: Int =3
  val showText: Boolean =false
  val showDesc: Boolean = false
  val useExtTrainWords = true
  val storeClearText: Boolean = false
}

class ArtiClusterPreparator extends Preparator[ArtiClusterTrainingData, ArtiClusterPreparaData] with Serializable{
  override def prepare(sc: JavaSparkContext, td: ArtiClusterTrainingData): ArtiClusterPreparaData = {
    val data = td.td
    val tokenizer = new Ansjpart()

    val idkeyWordRDD = data.map{ text =>
      val label = text._1
      var title = text._2
      var content = text._3
      var allcontent = title
      if (content != null){
        allcontent = title + content
      }
      val words =  tokenizer.splitWord(allcontent).toSeq
      (words, label, title)
     }
      .cache()

    val keyWordRDD = idkeyWordRDD.map(s => s._1)
    val wmodel = word2vecModel
    val model :Word2VecModel= wmodel.fit(keyWordRDD)
    val textVecter = idkeyWordRDD.filter(_._1.size != 0)
      .map(x => (new DenseVector(divArray(x._1.map(m => wordToVector(m, model, word2vecPrama.vectorSize).toArray).reduceLeft(sumArray),x._1.length)), x._2, x._3)).cache()
    val normalizerObj = new Normalizer()
    val normalTv = textVecter.map(x=>(normalizerObj.transform(x._1), x._2, x._3)).cache()

    new ArtiClusterPreparaData(normalTv, model)
  }

  def word2vecModel: Word2Vec= {
    val word2vec = new Word2Vec()
      .setSeed(word2vecPrama.seed)
      .setMinCount(word2vecPrama.minCount)
      .setLearningRate(word2vecPrama.learningRate)
      .setNumIterations(word2vecPrama.numIterations)
      .setVectorSize(word2vecPrama.vectorSize)
    word2vec
  }

  def sumArray (m: Array[Double], n: Array[Double]): Array[Double] = {
    for (i <- 0 until m.length) {m(i) += n(i)}
    return m
  }

  def divArray (m: Array[Double], divisor: Double) : Array[Double] = {
    for (i <- 0 until m.length) {m(i) /= divisor}
    return m
  }

  def wordToVector (w:String, m: Word2VecModel, s: Int): Vector = {
    try {
      return m.transform(w)
    } catch {
      case e: Exception => return Vectors.zeros(s)
    }
  }

}