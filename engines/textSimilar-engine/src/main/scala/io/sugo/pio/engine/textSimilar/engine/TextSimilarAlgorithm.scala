package io.sugo.pio.engine.textSimilar.engine

import io.sugo.pio.engine.textSimilar.data.{TextSimilarModelData, TextSimilarPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.ansj.app.keyword.KeyWordComputer
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.feature.{Normalizer, Word2Vec, Word2VecModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.DenseVector

import scala.collection.JavaConverters._
/**
  */
object  ap{
  val seed: Int = 75
  val minCount: Int = 1
  val learningRate: Double = 0.025
  val numIterations: Int = 1
  val vectorSize: Int = 100
  val minTokenSize: Int =3
  val showText: Boolean =false
  val showDesc: Boolean = false
  val useExtTrainWords = true
  val storeClearText: Boolean = false
}

class TextSimilarAlgorithm extends Algorithm[TextSimilarPreparaData, TextSimilarModelData] with Serializable{
  override def train(sc: JavaSparkContext, pd: TextSimilarPreparaData): TextSimilarModelData = {
    val keyNum = 100
    val simNum = 50
    val data = pd.pd

    val idkeyWordRDD = data.map{ text =>
      val title = text._1
      val content = text._2
      val id = text._3
      val kwc = new KeyWordComputer(keyNum);
      val keyword = kwc.computeArticleTfidf(title,content).asScala.toArray
        .map(s => s.getName).toSeq
      (keyword, id, title)
    }
    val keyWordRDD = idkeyWordRDD.map(s => s._1)
    val wmodel = word2vecModel
    val model = wmodel.fit(keyWordRDD)
    val textVecter = idkeyWordRDD.map(x => (new DenseVector(divArray(x._1.map(m => wordToVector(m, model, ap.vectorSize).toArray).reduceLeft(sumArray),x._1.length)).asInstanceOf[Vector] , x._2, x._3))
    val normalizer1 = new Normalizer()
    val normalTv = textVecter.map(x=>(normalizer1.transform(x._1), x._2, x._3))
      .map(x=>({new breeze.linalg.DenseVector(x._1.toArray)}, x._2, x._3)).cache()

    val idset = normalTv.map(s => (s._1, s._2)).collect()
    val resMap = new scala.collection.mutable.HashMap[String, Array[(Double, (String, String))]]()
    for((vec, id) <- idset) {
      val vectorFt = normalTv.filter(_._2 != id)
      val simScore = vectorFt.map(x=>(vec.dot(x._1),(x._2, x._3))).sortByKey(ascending = false).take(simNum)
      resMap.put(id, simScore)
    }
    new TextSimilarModelData(resMap)
  }

  def word2vecModel: Word2Vec= {
    val word2vec = new Word2Vec()
    .setSeed(ap.seed)
    .setMinCount(ap.minCount)
    .setLearningRate(ap.learningRate)
    .setNumIterations(ap.numIterations)
    .setVectorSize(ap.vectorSize)
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
