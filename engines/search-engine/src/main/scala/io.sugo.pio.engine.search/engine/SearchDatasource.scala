package io.sugo.pio.engine.search.engine

import java.sql.{DriverManager, ResultSet}

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import io.sugo.pio.engine.search.data.SearchTrainingData
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.search.Constants
import io.sugo.pio.engine.search.param.SearchDatasourceParams
import io.sugo.pio.engine.training.DataSource

import scala.collection.JavaConverters._

class SearchDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[SearchTrainingData]{

  override def readTraining(sc: JavaSparkContext): SearchTrainingData = {
    val originalRDD = propertyHose.find(sc).rdd.map(s => s.asScala)
      .map(itemInf => (itemInf(Constants.ITEM_ID).toString, itemInf(Constants.ITEM_NAME).toString) )

//    val htcData = batchEventHose.find(sc).rdd.map(s => s.getProperties.asScala.toMap)
//      .filter(!_.isEmpty)
//      .map{ pros =>
//        (pros(Constants.ITEM_ID).toString, pros(Constants.ITEM_NAME).toString)
//      }
    new SearchTrainingData(originalRDD)
  }
}

