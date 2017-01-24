package io.sugo.pio.engine.userHistory.engine

import java.sql.{DriverManager, ResultSet}

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.training.DataSource
import io.sugo.pio.engine.userHistory.Constants
import io.sugo.pio.engine.userHistory.data.UserHistoryTrainingData

import scala.collection.JavaConverters._

class UserHistoryDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[UserHistoryTrainingData]{

  override def readTraining(sc: JavaSparkContext): UserHistoryTrainingData = {
    val actionRdd = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
      .map(actinf => (actinf(Constants.USER_ID).toString, actinf(Constants.ITEM_ID).toString, actinf(Constants.TIME_NUM).toString.toLong))
    new UserHistoryTrainingData(actionRdd)
  }
}

