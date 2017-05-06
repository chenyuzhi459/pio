package io.sugo.pio.engine.userExtension.engine

import io.sugo.pio.engine.userExtension.data.{UserExtensionModelData, UserExtensionPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.mllib.clustering.{LDA, LDAModel}
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.rdd.RDD

class UserExtensionAlgorithm() extends Algorithm[UserExtensionPreparaData, UserExtensionModelData]{
  override def train(sc: JavaSparkContext, pd: UserExtensionPreparaData): UserExtensionModelData = {
    // 格式化数据
    val targetData: RDD[(String, Vector)] = pd.userProfileTarget
    val candidateData: RDD[(String, Vector)] = pd.userProfileCandidate


    // 构造训练数据集(抽样)并编号
    val trainData: RDD[(Long, Vector)] = candidateData
      .map(v => v._2)
      .zipWithIndex().map(_.swap)

    // 获取原向量空间的维度
    val featureNum: Int = candidateData.first()._2.size
    val k: Int = featureNum match {
      case n if n <= 10 => featureNum
      case _ => 10
    }

    // 开始训练
    val ldaModel: LDAModel = new LDA()
      .setMaxIterations(100)
      .setCheckpointInterval(5)
      .setK(k)  // TODO: 映射后的向量空间长度
      .run(trainData)
    new UserExtensionModelData(ldaModel, targetData, candidateData)
  }
}

