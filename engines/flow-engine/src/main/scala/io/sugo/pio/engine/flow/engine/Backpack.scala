package io.sugo.pio.engine.flow.engine
import breeze.linalg._
import io.sugo.pio.engine.flow.data.BackpackRes

import scala.collection.mutable.ArrayBuffer
/**
  */
class Backpack(totalFlow: Double, weightFlow: Array[Double], priceFlow: Array[Double]) {
  val max: Double = totalFlow + 1

  //背包法
  def calcuateFlowGroup(): BackpackRes={
    val weightFlowLocal = weightFlow.map(_.toInt)

    val nLen = weightFlowLocal.length
    val wide = nLen + 1
    val high = (totalFlow + 1).toInt
    var Table = DenseMatrix.zeros[Double](wide, high)
    var Path  = DenseMatrix.zeros[Double](wide, high)
    println("Tabel init...")
    for (i<- 1 to wide-1){
      for (j<-1 to high-1){
        Table(i,j) = max
      }
    }

    for (i<- 0 to wide-1){
      for (j<-0 to high-1){
        var wf: Int = 0
        var pf: Double = 0
        if (i < 1){
          Table(i,j) = Table(wide-1,j)
          wf = weightFlowLocal.last
          pf = priceFlow.last
        }
        else {
          Table(i,j) = Table(i-1, j)
          wf = weightFlowLocal(i-1)
          pf = priceFlow(i-1)
        }

        if ( (j >= wf) && (Table(i,j) >= (Table(i, j-wf) + pf)) ){
          Table(i,j) = Table(i, j-wf) + pf
          Path(i,j) = 1
        }
      }
    }

    println("get item from backpack...")
    var bag_result = new ArrayBuffer[Long]()
    var flag = true
    var iIndex = wide -1
    var jIndex = high -1
    var lastPrice = max
    while (lastPrice == max){
      lastPrice = Table(iIndex, jIndex)
    }

    while (iIndex>0 && jIndex >0){
      if (Path(iIndex,jIndex) ==1 && Path(iIndex,jIndex) != max){
        val itemWeight = weightFlowLocal(iIndex-1)
        bag_result.append(itemWeight)
        jIndex = jIndex - weightFlowLocal(iIndex-1)
      }
      else {
        iIndex = iIndex -1
      }
    }
    val countWeightFlow = bag_result.toArray
      .map(w => (w,1))
      .groupBy(_._1)
      .map{ case (word, list) =>
        (word, list.length)
    }
    println("min res:", lastPrice)
    println("group:", bag_result)
    println("count:", countWeightFlow)
    new BackpackRes(lastPrice.toString, countWeightFlow.toString())
  }

  //快速法
  def calcuateFast():BackpackRes= {
    val zipMap = (weightFlow zip priceFlow).toMap
    var index = -1
    var bag_result = new ArrayBuffer[Double]

    val cpsIndex = weightFlow.map { weg =>
      index = index + 1
      val dweg: Double = weg.toDouble
      val res = dweg / priceFlow(index)
      (res, index)
    }.sortWith((s1, s2) => s1._2 > s2._2)
      .map(_._2)

    var foolPool = totalFlow.toDouble

    var cIndex = 0
    while (foolPool >= weightFlow.min) {
      if (foolPool >= weightFlow(cpsIndex(cIndex))) {
        val item = weightFlow(cpsIndex(cIndex))
        foolPool = foolPool - item
        bag_result.append(item)
        cIndex = 0
      }
      else {
        cIndex = cIndex + 1
      }
    }
    val countWeightFlow = bag_result.toArray
      .map(w => (w, 1))
      .groupBy(_._1)
      .map { case (word, list) =>
        (word, list.length)
      }
    val minPrice = countWeightFlow.map{ case (wig, count) =>
        val price = zipMap(wig) * count
        price
      }.sum
    new BackpackRes(minPrice.toString, countWeightFlow.toString())
  }

}
