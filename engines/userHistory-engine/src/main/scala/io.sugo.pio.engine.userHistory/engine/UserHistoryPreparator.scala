package io.sugo.pio.engine.userHistory.engine

import io.sugo.pio.engine.training.Preparator
import io.sugo.pio.engine.userHistory.data.{UserHistoryPreparaData, UserHistoryTrainingData}
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class UserHistoryPreparator extends Preparator[UserHistoryTrainingData, UserHistoryPreparaData] {
  override def prepare(sc: JavaSparkContext, td: UserHistoryTrainingData): UserHistoryPreparaData = {
    new UserHistoryPreparaData(td.actData)
  }
}
