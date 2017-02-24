package io.sugo.pio.engine.articleClu.engine

import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

/**
  */
class RFModel(sc: JavaSparkContext, data:  RDD[(Vector, String, String)]) {
  val logger = Logger.getLogger(this.getClass)
  def train(): Unit ={
    val labebsMap = data.map(_._2).distinct().zipWithIndex().collect().toMap
    val wholeData = data.map(event => (event._1, event._2)).cache()
//    val kmeanData = kMean(sc, wholeData1, labebsMap)                    //聚类
//    val wholeData = downSample(sc, labebsMap, wholeData1).cache()       //下采样

    val wPoint = wholeData.map{ case (vec, label) =>
      LabeledPoint(labebsMap(label), vec)
    }.cache()
    val inputData = divideData(wPoint)

    val result = inputData.map{ data =>
        val trainData = data._1
        val testData  = data._2
        val model = trainModel(labebsMap, trainData)
        val lastR = predictModel(labebsMap, model, testData)
        lastR
      }

    logger.info("run is complete!")

  }

  def trainModel(labebsMap: Map[String, Long], trainData: RDD[LabeledPoint]): RandomForestModel = {
    val numClasses = labebsMap.size
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 10 // Use more in practice.
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini"
    val maxDepth = 5
    val maxBins = 3

    val model = RandomForest.trainClassifier(trainData, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
    model
  }

  def predictModel(labebsMap: Map[String, Long], model: RandomForestModel, testData: RDD[LabeledPoint]):(Double, Iterable[(Long, String, Double, Double, Double)]) ={
    val predictionAndLabels = testData.map{ case LabeledPoint(label, features) =>
      val prediction = model.predict(features)
      (prediction, label)
    }

    val metrics = new MulticlassMetrics(predictionAndLabels)

    val labels = labebsMap.map(_._2).toArray

    // Precision by threshold
    val accuracyResult = metrics.accuracy
    println("accuracy:", accuracyResult)

    val indexd = labebsMap.map{ case(title, label) =>
        var precision = - 0.1
        var recall =  - 0.1
        var fMea   =  -0.1
        try{
          precision = metrics.precision(label);
          recall = metrics.recall(label);
          fMea   = metrics.fMeasure(label);
          println("title=", title, " label=",label, " precision=", precision, " recall=", recall, "fm=", fMea)
        }
        catch{
          case e: NoSuchElementException => println(label ,"is not exists")
        }
        (label, title, precision, recall, fMea)
      }
    (accuracyResult, indexd)
  }

  def divideData(wdata: RDD[LabeledPoint]): Array[(RDD[LabeledPoint], RDD[LabeledPoint])]= {
    val kFold = 5
    val zipWholeData = wdata.zipWithUniqueId()
    val evaldata =  (0 until kFold).map { idx => {
      val trainingData = zipWholeData.filter(_._2 % kFold != idx).map(_._1)
      val testingData = zipWholeData.filter(_._2 % kFold == idx).map(_._1)
      (trainingData, testingData)
    }}.toArray
    evaldata
  }

  def kMean(sc: JavaSparkContext, data: RDD[(Vector, String)], labebsMap: Map[String, Long]): RDD[(Vector, String)]={
    val retainRate = 0.7
    val numClusters = 1
    val numIterations = 30
    var lastData = new ArrayBuffer[(Vector, String)]()

    labebsMap.map{ case (label, index) =>
      val labelData = data.filter(_._2 == label)
      val totalNum = labelData.count()
      val retainNum = (totalNum.toDouble * retainRate).toInt
      val trainData = labelData.map(s => Vectors.dense(s._1.toArray))
      val clusters = KMeans.train(trainData, numClusters, numIterations)
      val centerPointer = clusters.clusterCenters(0)

      import breeze.linalg.Vector
      val distanceRDD = data.map{ case(vec, label) =>
        val vecb = Vector(vec.toArray)
        val centerb = Vector(centerPointer.toArray)
        val vevbSqu = vecb :* vecb
        val centerbSqu = centerb :* centerb
        val distance = vevbSqu dot centerbSqu
        (distance, vec, label)
      }.sortBy(_._1)
        .take(retainNum)
        .map(s => (s._2, s._3))

      lastData = lastData ++ distanceRDD
    }
    val lastDataRdd = sc.parallelize(lastData)
    lastDataRdd
  }

  def downSample(sc: JavaSparkContext, labebsMap: Map[String, Long], wholeData: RDD[(Vector, String)]): RDD[(Vector, String)] = {
    val labelCount = labebsMap.map{ case(label, num) =>
      val count = wholeData.filter(_._2 == label).count()
      count
    }.toArray.min -1

    var dataArray: Array[(Vector, String)] = null

    var flag = 0
    val labelRdd = labebsMap.foreach{ case(label, num) =>
      val array = wholeData.filter(_._2 == label).takeSample(false, labelCount.toInt)
      if (0 == flag){
        dataArray = array
      }
      else {
        dataArray = dataArray ++ array
      }
      flag = flag + 1
    }
    val downSamplerdd = sc.parallelize(dataArray.toList)
    downSamplerdd
  }

}
