package io.sugo.pio.engine.fp.engine

import java.util.{List => jList}

import io.sugo.pio.engine.fp.data.{FpModelData, FpPreparaData}
import io.sugo.pio.engine.training.Algorithm
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.rdd.RDD

class FpAlgorithm() extends Algorithm[FpPreparaData, FpModelData]{
  override def train(sc: JavaSparkContext, pd: FpPreparaData): FpModelData = {
    val SEQ = ":"
    val minSupport = 0.01
    val numPartitions = 10
    val minConfidence = 0.5
    val training = pd.sessionData
    training.cache
    val model = run(training, minSupport, numPartitions)

    val mapFreqItems = model.freqItemsets.map{ fre =>
      val Items = fre.items.sorted
      val mkItems = Items.mkString(SEQ)
      val freq = fre.freq.toDouble
      Map(mkItems->freq)
    }
      .collect()
      .reduce((map1, map2) => map1 ++ map2 )

    val modelData = model.generateAssociationRules(minConfidence).map{ rule =>
      val antecedent = rule.antecedent.sorted
      val itemid = rule.consequent.mkString("")
      val confidence = rule.confidence
      val mkAntecedent = antecedent.mkString(SEQ)
      val weight = mapFreqItems(mkAntecedent) * confidence
      (itemid, antecedent, weight)
    }
    new FpModelData(modelData)
  }

  private def run(training: RDD[Array[String]],
                  minSupport: Double, numPartitions: Int) = {
    val fpg = new FPGrowth()
      .setMinSupport(minSupport)
      .setNumPartitions(numPartitions)
    fpg.run(training)
  }
}
