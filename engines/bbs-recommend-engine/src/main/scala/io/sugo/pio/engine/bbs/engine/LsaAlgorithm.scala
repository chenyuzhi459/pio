package io.sugo.pio.engine.bbs.engine

import breeze.linalg.{DenseVector, DenseMatrix => BDenseMatrix, svd => BSVD, _}
import org.apache.spark.ml.feature.{CountVectorizer, IDF}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.ml.linalg.{Vector => MLVector}
import org.apache.spark.sql.functions._
import breeze.linalg.{sum => BSUM}
import org.apache.log4j.Logger
import scala.collection.Map
import scala.util.control.Breaks
import org.apache.spark.mllib.linalg.{ Matrix, SingularValueDecomposition, Vectors, Vector => MLLibVector}
import org.apache.spark.mllib.linalg.distributed.RowMatrix

/**
  */
case class LsaModelResult(ksvdMat: BDenseMatrix[Double], idTerms: Map[String, Int], idDocs: Map[Long, String], termIdfs: Array[Double]) extends Serializable

class LsaAlgorithm(data: RDD[(String, Seq[String])], spksession: SparkSession, sc: JavaSparkContext, itemNumber: Long) extends Serializable{
  val logger = Logger.getLogger(this.getClass)
  def start(): LSAQueryEngine = {
    val numTerms = 5000
    val k = 1000
    import spksession.implicits._
    val orgdata = data.toDS()
    var (docTermMatrix, termIds, docIds, termIdfs) = documentTermMatrix(orgdata, numTerms, sc)
    docTermMatrix.cache()
    var vecRdd = docTermMatrix.select("tfidfVec").rdd.map { row =>
      val sparseVec = row.getAs[MLVector]("tfidfVec").toSparse
      Vectors.sparse(sparseVec.size, sparseVec.indices, sparseVec.values)
    }
    vecRdd.cache()
    val mat = new RowMatrix(vecRdd)
    val svd = mat.computeSVD(k, computeU=true)

    val US: Array[MLLibVector] = multiplyByDiagonalRowMatrix(svd.U, svd.s)
    val idTerms: Map[String, Int] = termIds.zipWithIndex.toMap

    val queryEngine = new LSAQueryEngine(svd, US, idTerms, docIds, termIdfs)
    queryEngine
  }

  def multiplyByDiagonalRowMatrix(mat: RowMatrix, diag: MLLibVector): Array[MLLibVector] = {
    val sArr = diag.toArray
    val res = new RowMatrix(mat.rows.map { vec =>
      val vecArr = vec.toArray
      val newArr = (0 until vec.size).toArray.map(i => vecArr(i) * sArr(i))
      Vectors.dense(newArr)
    })
    res.rows.collect()
  }

  def documentTermMatrix(terms : Dataset[(String, Seq[String])], numTerms: Int, sc: JavaSparkContext)
  : (DataFrame, Array[String], Map[Long, String], Array[Double]) = {
    import spksession.implicits._
    val termsDF = terms.toDF("title", "terms")
    val filtered = termsDF
    val countVectorizer = new CountVectorizer()
      .setInputCol("terms").setOutputCol("termFreqs").setVocabSize(numTerms)
    val vocabModel = countVectorizer.fit(filtered)
    val docTermFreqs = vocabModel.transform(filtered)
    val termIds = vocabModel.vocabulary
    docTermFreqs.cache()
    val docIdsDF = docTermFreqs.withColumn("id", monotonically_increasing_id)
    val docIds = docIdsDF.select("id", "title").as[(Long, String)].collect().toMap
    val idf = new IDF().setInputCol("termFreqs").setOutputCol("tfidfVec")
    val idfModel = idf.fit(docTermFreqs)
    val docTermMatrix = idfModel.transform(docTermFreqs).select("title", "tfidfVec")
    (docTermMatrix, termIds, docIds, idfModel.idf.toArray)
  }

  def ksvd(mat: BDenseMatrix[Double]): BDenseMatrix[Double] = {
    val svdRes = BSVD(mat)
    val k = getKvalue(svdRes.S)
    val uk = svdRes.U(::, 0 to k)
    val sk = diag(svdRes.S(0 to k))
    val vtk = svdRes.Vt(0 to k, ::)
    uk * sk * vtk
  }

  def getKvalue (vec: DenseVector[Double]): Int = {
    var k = 0
    val thred = 0.7
    var buf: Double = 0
    val sumRes = BSUM(vec)
    val loop = new Breaks
    loop.breakable {
      while (true){
        if (k > (vec.length-1)){
          loop.break()
        }
        val kSumSinglarValue = BSUM(vec(0 to k))
        if ( (kSumSinglarValue/sumRes) > thred ){
          loop.break()
        }
        k = k + 1
      }
    }
    logger.info(s"Adaptive determine the topic number is $k")
    k
  }

}

class LSAQueryEngine(
                      val svd: SingularValueDecomposition[RowMatrix, Matrix],
                      val US: Array[MLLibVector],
                      val idTerms: Map[String, Int],
                      val docIds: Map[Long, String],
                      val termIdfs: Array[Double]) extends Serializable{ }
