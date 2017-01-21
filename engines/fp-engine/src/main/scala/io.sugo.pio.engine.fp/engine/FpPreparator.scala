package io.sugo.pio.engine.fp.engine

import io.sugo.pio.engine.fp.data.{FpPreparaData, FpTrainingData}
import io.sugo.pio.engine.training.Preparator
import org.apache.spark.api.java.JavaSparkContext

/**
  */
class FpPreparator extends Preparator[FpTrainingData, FpPreparaData] {
  override def prepare(sc: JavaSparkContext, td: FpTrainingData): FpPreparaData = {
    new FpPreparaData(td.sessionData)
  }
}
