package io.sugo.pio.engine.userHistory.engine

import java.util
import java.util.{List => jList}

import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.training.Algorithm
import io.sugo.pio.engine.userHistory.data.{UserHistoryModelData, UserHistoryPreparaData}

class UserHistoryAlgorithm() extends Algorithm[UserHistoryPreparaData, UserHistoryModelData]{
  override def train(sc: JavaSparkContext, pd: UserHistoryPreparaData): UserHistoryModelData = {
    new UserHistoryModelData(pd.preData)
  }
}

