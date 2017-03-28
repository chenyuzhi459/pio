package io.sugo.pio.engine.bbs.engine

import org.ansj.splitWord.analysis._
import org.ansj.recognition.impl.StopRecognition
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import breeze.linalg.{DenseMatrix => BDenseMatrix, SparseVector => BSparseVector}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Matrices

import scala.io.Source
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  */
class AnsjpartBbs() extends Serializable{
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
    var fitler:StopRecognition = new StopRecognition()
    val inputStream = this.getClass.getClassLoader.getResource(stopPath).getPath
    val lines = Source.fromFile(inputStream).getLines()
    while (lines.hasNext){
      fitler.insertStopWords(lines.next())
    }
    fitler
  }

  def text2words(text: String, toAnalysis: ToAnalysis): Array[String] = {
    val parse = toAnalysis.parseStr(text)
    val words = parse.getTerms.asScala.toArray.map(s => s.getName.trim)
      .filter{ string =>
        (string != null) && (string != "") && (string != " ")
      }
    words
  }

}

class BbsPredict(lsa: LSAQueryEngine, query: Array[String]) extends Serializable{
  def predict(sc: SparkContext): (Array[String], Array[String]) ={
    val cc = sc.parallelize(List(1,2,3))
    val res = getTopDocsForTermQuery(query.toSet.toSeq, sc)
    val itemRes = res._1.map(_._1)
    val articleRes = res._2.map(_._1)
    (itemRes, articleRes)
  }

  def getTopDocsForTermQuery(terms: Seq[String], sc: SparkContext): (Array[(String, Double)], Array[(String, Double)]) = {
    val queryVec = termsToQueryVector(terms)
    val idWeights = topDocsForTermQuery(queryVec, sc)
    val itemId = idWeights.map{ case (score, id) => (lsa.docIds(id), score) }
    val items = itemId.filter(_._1.contains("item:")).sortWith((s1,s2) => s1._2 > s2._2).take(10)
    val artices = itemId.filter(_._1.contains("article:")).sortWith((s1,s2) => s1._2 > s2._2).take(10)
    (items, artices)
  }

  def termsToQueryVector(terms: Seq[String]): BSparseVector[Double] = {
    val indices = getTermsIndice(terms)
    val values = indices.map(lsa.termIdfs(_))
    new BSparseVector[Double](indices, values, lsa.idTerms.size)
  }

  def getTermsIndice(terms: Seq[String]): Array[Int]={
    val indices = new ArrayBuffer[Int]()
    terms.foreach{term =>
      try{
        val termId = lsa.idTerms(term)
        indices.append(termId)
      }catch {
        case e:NoSuchElementException => {println("NoSuchElementException", term)}
      }
    }
    indices.toArray
  }

  def topDocsForTermQuery(query: BSparseVector[Double], sc: SparkContext): Array[(Double, Long)] = {
    val breezeV = new BDenseMatrix[Double](lsa.svd.V.numRows, lsa.svd.V.numCols, lsa.svd.V.toArray)
    val termRowArr = (breezeV.t * query).toArray
    val termRowVec = Matrices.dense(termRowArr.length, 1, termRowArr)
    // Compute scores against every doc
    val usRdd = sc.parallelize(lsa.US)
    val mat = new RowMatrix(usRdd)
    val docScores = mat.multiply(termRowVec)
    // Find the docs with the highest scores
    val allDocWeights = docScores.rows.map(_.toArray(0)).zipWithUniqueId
    allDocWeights.collect()
  }

}
