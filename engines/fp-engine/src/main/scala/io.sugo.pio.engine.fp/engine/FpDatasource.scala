package io.sugo.pio.engine.fp.engine

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.fp.Constants
import io.sugo.pio.engine.fp.data.FpTrainingData
import io.sugo.pio.engine.training.DataSource
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD
import org.joda.time.DateTime
import scala.collection.JavaConverters._

class FpDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[FpTrainingData]{

  override def readTraining(sc: JavaSparkContext): FpTrainingData = {
    val minEffectRate = 4.0
    val actionRdd = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
        .map{ actInf =>
              val userId = actInf(Constants.USERID).toString
              val itemId = actInf(Constants.ITEMID).toString
              val rate = actInf(Constants.GRADE).toString.toDouble
              val time = actInf(Constants.TIMENUM).toString.toLong

              val dt = new DateTime(time)
              val year = dt.getYear.toString
              val month = dt.getMonthOfYear.toString
              val day = dt.getDayOfMonth.toString
              val hour = dt.getHourOfDay.toString
              val session = year ++ month ++ day ++ hour
              if (rate >= minEffectRate){
                (userId, itemId, session)
              }
              else {
                null
              }
        }
      .filter(_ != null)

    val groups = group(actionRdd)
    new FpTrainingData(groups)
  }

  private def group(rdd: RDD[(String, String, String)]) = {
    val maxNumRate = 10
    val minNumRate = 2

    val sessionRDD = rdd.map(s => ((s._3, s._1), s._2)).groupByKey()
      .filter( s => (s._2.size <=maxNumRate) && (s._2.size >= minNumRate) )
      .map(s => s._2.toArray)
    sessionRDD
  }
}

