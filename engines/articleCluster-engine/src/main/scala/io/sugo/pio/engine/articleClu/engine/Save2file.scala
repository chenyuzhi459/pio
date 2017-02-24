package io.sugo.pio.engine.articleClu.engine

import java.io.{File, PrintWriter}

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  */
class Save2file(data :RDD[(Seq[String], String, String)]) extends Serializable{
  def savetxt: Unit ={
//    val path = this.getClass.getClassLoader.getResource("vocabulary/test.txt").getPath
    val path1 = "trainData.txt"
    var writer = new PrintWriter(new File(path1), "UTF-8")
    val Array(dataArray, testArray) = data.map(s => (s._2, s._1)).randomSplit(Array(0.6, 0.4), seed = 11L)
    dataArray.collect().map{ case (label, vocaSeq) =>
      val fastLabel = "__label__" ++ label ++ ", "
      val fastFeature = vocaSeq.mkString(" ")
      print(fastFeature)
      val fastLine = (fastLabel ++ fastFeature).replace("\n", " ")
      writer.write(fastLine + "\n")
    }
    writer.close()

    val path2 = "testData.txt"
    writer = new PrintWriter(new File(path2), "UTF-8")
    testArray.collect().map{ case (label, vocaSeq) =>
      val fastLabel = "__label__" ++ label ++ ", "
      val fastFeature = vocaSeq.mkString(" ")
      print(fastFeature)
      val fastLine = (fastLabel ++ fastFeature).replace("\n", " ")
      writer.write(fastLine + "\n")
    }
    writer.close()
    print("ok")
  }

  def savedata(data: RDD[LabeledPoint], file_name: String): Unit = {
    val path = file_name
    var writer = new PrintWriter(new File(path), "UTF-8")
    val arrayData = data.collect()
    arrayData.foreach{ case (LabeledPoint(label, vec)) =>
        val labInt = label.toInt.toString
        val vecString = vec.toArray.mkString(",")
        val line = labInt ++ "," ++ vecString
      writer.write(line + "\n")
    }
    writer.close()
  }
}
