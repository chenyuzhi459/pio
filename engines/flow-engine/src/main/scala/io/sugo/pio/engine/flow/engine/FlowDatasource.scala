package io.sugo.pio.engine.flow.engine

import java.util

import io.sugo.pio.engine.data.input.{BatchEventHose, PropertyHose}
import org.apache.spark.api.java.JavaSparkContext
import io.sugo.pio.engine.flow.data.FlowTrainingData
import io.sugo.pio.engine.flow.eval.{FlowEvalActualResult, FlowEvalQuery}
import io.sugo.pio.engine.training.DataSource

class FlowDatasource(propertyHose: PropertyHose, batchEventHose: BatchEventHose) extends DataSource[FlowTrainingData, FlowEvalQuery, FlowEvalActualResult] {

  override def readTraining(sc: JavaSparkContext): FlowTrainingData = {
    //test code
    val totalFlow: Double = 10585702
    val weightFlow: Array[Double] = Array(1,150, 300, 500, 1024, 1228, 1433, 1638, 2048)
    val priceFlow: Array[Double]  = Array(1.06,6.88, 10.58, 15.88, 31.75, 34.93, 38.1, 41.28, 47.63)
    val backpackObj = new Backpack(totalFlow, weightFlow, priceFlow)
    backpackObj.calcuateFast()
    new FlowTrainingData()
  }

  override def readEval(sc: JavaSparkContext): util.List[(FlowTrainingData, FlowEvalQuery, FlowEvalActualResult)] = {
    null
  }
}


