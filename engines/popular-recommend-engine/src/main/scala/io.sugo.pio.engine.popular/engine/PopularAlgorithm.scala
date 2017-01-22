package io.sugo.pio.engine.popular.engine

import java.util.{List => jList}

import io.sugo.pio.engine.popular.Constants
import io.sugo.pio.engine.popular.data.{PopularModelData, PopularPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._

class PopularAlgorithm() extends Algorithm[PopularPreparaData, PopularModelData]{
  override def train(sc: JavaSparkContext, pd: PopularPreparaData): PopularModelData = {
    val itemPopular = popularItemCatetory(pd.actionData)
    val itemInfo = pd.itemData.map({
      m =>
        (m(Constants.ITEM_ID).asInstanceOf[Int], m(Constants.CATEGORY).asInstanceOf[jList[String]].asScala.toList)
    })
    new PopularModelData(itemPopular.join(itemInfo))
  }

  def popularItemCatetory(bhvRDD:RDD[Map[String, AnyRef]]): RDD[(Int, Int)]={
    val items = bhvRDD.map(m => (m.get(Constants.ITEM_ID).get.asInstanceOf[Int], 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, false)
    items
  }
}

