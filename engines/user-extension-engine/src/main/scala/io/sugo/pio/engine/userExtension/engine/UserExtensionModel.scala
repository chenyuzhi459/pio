package io.sugo.pio.engine.userExtension.engine

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import io.sugo.pio.engine.common.data.UserExtensionRepository
import io.sugo.pio.engine.data.output.Repository
import io.sugo.pio.engine.userExtension.data.UserExtensionModelData
import io.sugo.pio.engine.training.Model
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.clustering.{DistributedLDAModel, LocalLDAModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

/**
  */
class UserExtensionModel(val repository: Repository) extends Model[UserExtensionModelData] with Serializable{
  override def save(jsc: JavaSparkContext,md: UserExtensionModelData): Unit = {
    val sc: SparkContext = JavaSparkContext.toSparkContext(jsc)
    // 存储模型
    val modelPath = repository.asInstanceOf[UserExtensionRepository].getModelDistancePath
    md.model.save(sc, modelPath)

    // 将所有数据映射到一个向量空间
    val modelDist: DistributedLDAModel = DistributedLDAModel.load(JavaSparkContext.toSparkContext(sc), modelPath)
    val modelLocal: LocalLDAModel = modelDist.toLocal
    val targetDataNew = md.targetData.map(v => (v._1, modelLocal.topicDistribution(v._2)))
    val candidateDataNew = md.candidateData.map(v => (v._1, modelLocal.topicDistribution(v._2)))
//    // 打印出来看看结果
//    var len: Int = -1
//    for (v <- targetDataNew.collect()) {
//      val uid = v._1
//      val valueStr = v._2.toArray.map(v1 => "%.2f".format(v1)).mkString(" ")
//      println("%s:%s".format(uid, valueStr))
//    }

    // 在 target 集合中找出向量空间里每个维度 Top1 的点作为中心点
    val k: Int = targetDataNew.first()._2.size
    val centers: ListBuffer[Vector] = ListBuffer.empty[Vector]
    for (i: Int <- 0 until k) {
      centers += targetDataNew.map(v => (v._2(i), (v._1, v._2))).sortByKey(false).first()._2._2
    }
    val centersBroadcast =  sc.broadcast(centers.toList)

    // 计算 candidate 数据集中每个点距离中心点最近的距离, 然后排序取 TopN
    val maxUser: Int = repository.asInstanceOf[UserExtensionRepository].getMaxUser
    val simUser: Array[(String, Vector)] = candidateDataNew.map(v => (nearest(v._2, centersBroadcast.value), v))
      .sortByKey(false)
      .take(maxUser)
      .map(v => v._2)

    // 存储数据
    repository.asInstanceOf[UserExtensionRepository].createPredictDistanceFile()
    val predictFile = repository.asInstanceOf[UserExtensionRepository].getPredictDistanceFile
    val writer: PrintWriter = new PrintWriter(new File(predictFile))

    for (v: (String, Vector) <- simUser) {
      val str: String = "%s:%s".format(v._1, v._2.toString)
//      println(str)
      writer.append(str).write("\n")
    }
    writer.flush()
    writer.close()
    centersBroadcast.unpersist()
  }

  def nearest(vector: Vector, centers: List[Vector]): Double = {
    var dist: Double = -1.0
    for (cv: Vector <- centers) {
      val d: Double = distance(vector, cv)
      if (d > dist) {
        dist = d
      }
    }
    dist
  }

  def distance(v1: Vector, v2: Vector): Double = {
    val arr1: Array[Double] = v1.toArray
    val arr2: Array[Double] = v2.toArray
    val k: Int = arr1.length

    val x: Double = arr1.zip(arr2).map(v => v._1 * v._2).sum
    val y1: Double = math.sqrt(arr1.map(v => math.pow(v, 2)).sum)
    val y2: Double = math.sqrt(arr2.map(v => math.pow(v, 2)).sum)
    x / (y1 * y2)
  }
}
